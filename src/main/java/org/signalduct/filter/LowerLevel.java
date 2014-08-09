package org.signalduct.filter;

import org.signalduct.Connection;

/**
 * Used to send messages towards the lower level filters and the network from a filter.
 */
public interface LowerLevel {

    /**
     * @param message message to send to the next lower level filter or the network.
     */
    void sendMessage(Connection connection, Object message);

    /**
     * Request disconnect from the remote endpoint.
     */
    void sendDisconnect(Connection connection);

}
