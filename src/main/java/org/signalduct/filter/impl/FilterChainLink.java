package org.signalduct.filter.impl;

import org.signalduct.Connection;
import org.signalduct.ConnectionListener;
import org.signalduct.filter.LowerLevel;
import org.signalduct.filter.MessageFilter;

import static org.flowutils.Check.notNull;

/**
 * Wrapper class around a messageFilter, that keeps track of adjacent higher and lower level message consumers,
 * so that the filter object itself doesn't have to manage the chain.
 *
 * This class is package protected, as it is not needed outside this package.
 */
final class FilterChainLink implements LowerLevel, ConnectionListener {

    private final MessageFilter messageFilter;
    private LowerLevel lowerLevel;
    private ConnectionListener higherLevel;

    protected FilterChainLink(MessageFilter messageFilter, LowerLevel lowerLevel, ConnectionListener higherLevel) {
        notNull(messageFilter, "messageFilter");

        this.messageFilter = messageFilter;

        setLowerLevel(lowerLevel);
        setHigherLevel(higherLevel);
    }

    protected void setLowerLevel(LowerLevel lowerLevel) {
        notNull(lowerLevel, "lowerLevel");

        this.lowerLevel = lowerLevel;
    }

    protected void setHigherLevel(ConnectionListener higherLevel) {
        notNull(higherLevel, "higherLevel");

        this.higherLevel = higherLevel;
    }

    protected MessageFilter getMessageFilter() {
        return messageFilter;
    }

    @Override public void onConnected(Connection connection) {
        messageFilter.receiveConnect(connection, higherLevel, lowerLevel);
    }

    @Override public void onMessage(Connection connection, Object message) {
        messageFilter.receiveMessage(connection, message, higherLevel, lowerLevel);
    }

    @Override public void sendMessage(Connection connection, Object message) {
        messageFilter.sendMessage(connection, message, lowerLevel, higherLevel);
    }

    @Override public void onDisconnected(Connection connection) {
        messageFilter.receiveDisconnect(connection, higherLevel);
    }

    @Override public void sendDisconnect(Connection connection) {
        messageFilter.sendDisconnect(connection, lowerLevel);
    }

    @Override public void onError(Connection connection, String errorType, String errorDescription) {
        // Forward errors up to the application
        higherLevel.onError(connection, errorType, errorDescription);
    }
}
