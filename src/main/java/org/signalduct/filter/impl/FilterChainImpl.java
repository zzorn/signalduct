package org.signalduct.filter.impl;

import org.flowutils.Check;
import org.signalduct.Connection;
import org.signalduct.ConnectionListener;
import org.signalduct.filter.FilterChain;
import org.signalduct.filter.LowerLevel;
import org.signalduct.filter.MessageFilter;

import java.util.*;

import static org.flowutils.Check.notNull;

/**
 * This implementation is thread safe for concurrent additions or removals of filters.
 *
 * Network event processing is also thread safe as long as the MessageFilters, the provided network and application level
 * listeners, and the Connection objects are.
 */
public final class FilterChainImpl implements FilterChain {

    private final LinkedList<FilterChainLink> links = new LinkedList<FilterChainLink>();

    private LowerLevel networkLevel;
    private ConnectionListener applicationLevel;

    private boolean initialized = false;
    private final Object chainUpdateLock = new Object();

    @Override public void initialize(LowerLevel networkLevel, ConnectionListener applicationLevel) {
        Check.notNull(networkLevel, "networkLevel");
        Check.notNull(applicationLevel, "applicationLevel");

        this.networkLevel = networkLevel;
        this.applicationLevel = applicationLevel;

        initialized = true;
    }

    @Override public void deInitialize() {
        initialized = false;

        this.networkLevel = null;
        this.applicationLevel = null;
    }

    @Override public void filterReceivedConnect(Connection connection) {
        Check.notNull(connection, "connection");
        checkInitialized();
        getReceiveChainStart().onConnected(connection);
    }

    @Override public void filterReceivedMessage(Connection connection, Object message) {
        Check.notNull(connection, "connection");
        checkInitialized();
        getReceiveChainStart().onMessage(connection, message);
    }

    @Override public void filterSentMessage(Connection connection, Object message) {
        Check.notNull(connection, "connection");
        checkInitialized();
        getSendChainStart().sendMessage(connection, message);
    }

    @Override public void filterReceivedDisconnect(Connection connection) {
        Check.notNull(connection, "connection");
        checkInitialized();
        getReceiveChainStart().onDisconnected(connection);
    }

    @Override public void filterSentDisconnect(Connection connection) {
        Check.notNull(connection, "connection");
        checkInitialized();
        getSendChainStart().sendDisconnect(connection);
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("The FilterChain is not yet initialized with the network and application levels, " +
                                            "can not process messages.  Call init() first on the FilterChain.");
        }
    }

    private ConnectionListener getReceiveChainStart() {
        if (links.isEmpty()) return applicationLevel;
        else return links.getFirst();
    }

    private LowerLevel getSendChainStart() {
        if (links.isEmpty()) return networkLevel;
        else return links.getLast();
    }



    @Override public void addFilterFirst(MessageFilter filter) {
        addFilterAt(filter, 0);
    }

    @Override public void addFilterLast(MessageFilter filter) {
        addFilterAt(filter, -1);
    }

    @Override public void addFilterAt(MessageFilter filter, int index) {
        notNull(filter, "filter");

        synchronized (chainUpdateLock) {
            // Determine index to add new link at
            index = calculateFilterIndex(index);

            // Get adjacent links
            FilterChainLink lowerLevelLink = index <= 0 ? null : links.get(index - 1);
            FilterChainLink higherLevelLink = index >= links.size() ? null : links.get(index);

            // Create new link
            final FilterChainLink newFilterChainLink = createFilterChainLink(filter, lowerLevelLink, higherLevelLink);

            // Update adjacent links
            if (lowerLevelLink != null) lowerLevelLink.setHigherLevel(newFilterChainLink);
            if (higherLevelLink != null) higherLevelLink.setLowerLevel(newFilterChainLink);

            // Add new link at correct place
            links.add(index, newFilterChainLink);
        }
    }

    private int calculateFilterIndex(int index) {
        // Negative values means add from end
        if (index < 0) index = links.size() + 1 + index;

        // Check result
        if (index < 0 || index >= links.size()) {
            throw new IllegalArgumentException("When trying to add a filter at a specific position, " +
                                               "the index " + index + " was out of bounds for the list of existing filters, " +
                                               "which is " + links.size() + " units long");
        }

        return index;
    }

    private FilterChainLink createFilterChainLink(MessageFilter filter,
                                                  FilterChainLink lowerLevelLink,
                                                  FilterChainLink higherLevelLink) {
        // Determine the higher and lower levels
        LowerLevel lowerLevel = lowerLevelLink == null ? networkLevel : lowerLevelLink;
        ConnectionListener higherLevel = higherLevelLink == null ? applicationLevel : higherLevelLink;

        // Create link
        return new FilterChainLink(filter, lowerLevel, higherLevel);
    }


    @Override public void removeFilter(MessageFilter filter) {
        synchronized (chainUpdateLock) {
            // Remove all links with a matching filter
            for (Iterator<FilterChainLink> iterator = links.iterator(); iterator.hasNext(); ) {
                FilterChainLink link = iterator.next();

                if (link.getMessageFilter() == filter) iterator.remove();
            }
        }
    }

    @Override public List<MessageFilter> getFilters() {
        List<MessageFilter> messageFilters = new ArrayList<MessageFilter>();

        synchronized (chainUpdateLock) {
            // Get all message filters from the links
            for (FilterChainLink link: links) {
                messageFilters.add(link.getMessageFilter());
            }
        }

        return messageFilters;
    }


}
