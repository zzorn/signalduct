package org.signalduct.filter;

import org.signalduct.Connection;
import org.signalduct.ConnectionListener;

/**
 * Represents a set of filters that can be applied to incoming and outgoing messages.
 */
public interface FilterChain extends MessageFiltering {

    /**
     * Tells the FilterChain where to forward messages intended for the network or application level.
     * Should be called before any filter methods are called.
     *
     * @param networkLevel filtered events to be sent over the network are forwarded to this object.
     * @param applicationLevel filtered events to be received by the application are forwarded to this object.
     */
    void initialize(LowerLevel networkLevel, ConnectionListener applicationLevel);

    /**
     * Can be called when the FilterChain is removed from active use.
     * Removes references to the network and application levels.
     */
    void deInitialize();

    /**
     * Apply filters to a connect event received from the network.
     * @param connection the connection that the event happened on.
     */
    void filterReceivedConnect(Connection connection);

    /**
     * Apply filters to a message received from the network.
     * @param connection the connection that the event happened on.
     */
    void filterReceivedMessage(Connection connection, Object message);

    /**
     * Apply filters to a message sent from the application to the network.
     * @param connection the connection that the event happened on.
     */
    void filterSentMessage(Connection connection, Object message);

    /**
     * Apply filters to a disconnect event received from the network.
     * @param connection the connection that the event happened on.
     */
    void filterReceivedDisconnect(Connection connection);

    /**
     * Apply filters to a disconnect event sent from the application to the network.
     * @param connection the connection that the event happened on.
     */
    void filterSentDisconnect(Connection connection);
}
