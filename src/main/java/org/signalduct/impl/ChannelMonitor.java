package org.signalduct.impl;

import org.flowutils.Check;
import org.flowutils.StringUtils;
import org.signalduct.utils.ExceptionUtils;

import java.io.IOException;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Monitors new events on one or more channels, and delegates handling of the events to a derived class.
 */
public abstract class ChannelMonitor<T extends SelectableChannel> {

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final ConcurrentHashMap<T, SelectionKey> channels = new ConcurrentHashMap<T, SelectionKey>();

    private Selector selector;

    protected ChannelMonitor() {
        selector = createSelector();
    }

    private Selector createSelector()  {
        final Selector selector;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new IllegalStateException("Could not create a selector for listening to network connections" + ExceptionUtils.getCauseDescription(e), e);
        }

        if (selector == null) throw new IllegalStateException("Could not create a selector for listening to network connections, null returned");

        return selector;
    }

    /**
     * Listen to events from the specified channel
     *
     * @param channel a channel that should be configured as non-blocking
     */
    public final void listenToChannelEvents(T channel) throws NetworkException {
        Check.notNull(channel, "channel");

        try {
            // Listen to all valid network events:
            SelectionKey selectionKey = channel.register(selector, channel.validOps());

            // Store channel
            channels.put(channel, selectionKey);

        } catch (IOException e) {
            throw createNetworkException("Could not start listening to channel " + channel.getClass().getSimpleName(), e);
        }
    }

    /**
     * Starts a listener loop in a new thread that listens to events from the channel.
     * @throws NetworkException if there was some problem with setting up the listener.
     */
    public void start() throws NetworkException {
        if (started.getAndSet(true)) throw new IllegalStateException(getClass().getSimpleName() + " already started, can't start twice!");

        Thread connectionListenerThread = new Thread(new Runnable() {
            @Override public void run() {
                runNetworkEventHandlingLoop();
            }
        });
        connectionListenerThread.setDaemon(true);
        connectionListenerThread.start();
    }


    private void runNetworkEventHandlingLoop() {
        while(!stopped.get()) {
            try {
                handleNetworkEvents();
            } catch (Throwable e) {
                reportNetworkEventException("Problem when trying to handle network events for selector " + selector, e);
            }
        }
    }

    private void handleNetworkEvents() throws IOException {
        int numberOfNetworkEvents = selector.select();
        if (numberOfNetworkEvents > 0) {
            final Set<SelectionKey> networkEvents = selector.selectedKeys();
            for (SelectionKey networkEventType : networkEvents) {

                // Get channel that the event is for
                final T channel = (T) networkEventType.channel();

                // Delegate event to the correct handler
                tryToHandleNetworkEvent(networkEventType, channel);
            }

            // The set returned by selectedKeys() need to be emptied by the calling code after processing:
            networkEvents.clear();
        }
    }

    private void tryToHandleNetworkEvent(SelectionKey networkEventType, T channel) {
        try {
            handleNetworkEvent(networkEventType, channel);
        } catch(Throwable e) {
            String description = "Problem when trying to handle a " + describeNetworkEventType(networkEventType) + " " +
                                 "for channel " + channel.toString();
            reportNetworkEventException(description, e);
        }
    }

    private String describeNetworkEventType(SelectionKey networkEventType) {
        String description = "network event";

        if (networkEventType.isAcceptable()) description += " with a new incoming connection";
        if (networkEventType.isConnectable()) description += " with a completed connection";
        if (networkEventType.isReadable()) description += " with incoming data to read";
        if (networkEventType.isWritable()) description += " indicating the connection is ready to receive outgoing data";

        return description;
    }

    /**
     * Handles an exception that happened in network event handling code.
     * It can not be thrown upwards, because network events are handled in a separate thread.
     * By default logs the exception.
     */
    protected void reportNetworkEventException(String description, Throwable e) {
        String message = description + ": " + ExceptionUtils.getCauseDescription(e);

        // TODO: Log & report error to listeners
        System.out.println(message);

        e.printStackTrace();
        // TODO: In case of DDoS type attack that generates a lot of errors, or other permanent error condition,
        // avoid filling up logs with errors if we constantly get exceptions
    }


    private void handleNetworkEvent(SelectionKey networkEventType, T channel) throws Exception {
        if (networkEventType.isAcceptable()) {
            acceptNewConnection(channel);
        }

        if (networkEventType.isConnectable()) {
            handleConnectionCreated(channel);
        }

        if (networkEventType.isReadable()) {
            readIncomingData(channel);
        }

        if (networkEventType.isWritable()) {
            writeOutgoingData(channel);
        }
    }

    /**
     * Called when a remote client connected to us.
     * Override if the implementing class can handle this type of events.
     */
    protected void acceptNewConnection(T channel) throws Exception {
        throw createUnsupportedEventException("accept new connection");
    }

    /**
     * Called when a connection to a remote server is established.
     * Override if the implementing class can handle this type of events.
     */
    protected void handleConnectionCreated(T channel) throws Exception {
        throw createUnsupportedEventException("new connection");
    }

    /**
     * Called when the specified channel has incoming data to read.
     * Override if the implementing class can handle this type of events.
     */
    protected void readIncomingData(T channel) throws Exception {
        throw createUnsupportedEventException("incoming data");
    }

    /**
     * Called when the specified channel is ready to receive outgoing data.
     * Override if the implementing class can handle this type of events.
     */
    protected void writeOutgoingData(T channel) throws Exception {
        throw createUnsupportedEventException("outgoing data");
    }

    private IllegalStateException createUnsupportedEventException(String eventType) {
        return new IllegalStateException(getClass().getSimpleName() + " does not expect "+ eventType +" network events");
    }

    /**
     * Stops the thread listening to network events, and closes the selector.
     * Also closes all the channels.
     * @throws NetworkException
     */
    public void stop() throws NetworkException {
        stopConnectionListenerThread();
        closeSelector();
        closeChannels();
    }

    private void stopConnectionListenerThread() {
        stopped.set(true);

        // Wake up the selector so that the main loop can retest the stop condition
        if (selector != null) selector.wakeup();
    }

    private void closeSelector() throws NetworkException {
        try {
            if (selector != null) selector.close();
        } catch (IOException e) {
            throw createNetworkException("Could not close selector", e);
        }
    }

    private void closeChannels() throws NetworkException {
        List<String> problems = null;
        for (T channel : channels.keySet()) {
            try {
                // Try to close a connection
                channel.close();

            } catch (IOException e) {
                final String problem = e.getClass().getSimpleName() + ": " + e.getMessage();

                // Add problem to list of problems
                if (problems == null) problems = new ArrayList<String>();
                problems.add(problem);
            }
        }

        // Report any encountered problems
        if (problems != null) throw createNetworkException("Problem when closing " + problems.size() + " connection(s): " +
                                                           StringUtils.collectionToString(problems));

        // Remove references to all channels
        channels.clear();
    }

    /**
     * @return a new NetworkException with the specified message and prefixed by the error prefix for this class.
     */
    protected final NetworkException createNetworkException(final String message) {
        return createNetworkException(message, null);
    }

    /**
     * @return a new NetworkException with the specified message and cause, and prefixed by the error prefix for this class.
     */
    protected final NetworkException createNetworkException(final String message, IOException e) {
        return new NetworkException(errorPrefix() + ": " + message, e);
    }

    /**
     * @return a prefix to append to the start of any error messages in errors thrown by this class.
     */
    protected String errorPrefix() {
        return this.getClass().getSimpleName();
    }

}
