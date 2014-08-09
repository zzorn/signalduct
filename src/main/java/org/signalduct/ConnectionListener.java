package org.signalduct;

import org.signalduct.Connection;

/**
 * Listens to events from the network and lower level filters.
 */
public interface ConnectionListener {

    /**
     * Called when a message is received from the network, or a lower level filter.
     */
    void onMessage(Connection connection, Object message);

    /**
     * Called when we complete a network connection, or a lower level filter completes connection handshaking.
     */
    void onConnected(Connection connection);

    /**
     * Called when we are disconnected from the network, or a lower level filter indicates a disconnection.
     */
    void onDisconnected(Connection connection);

    /**
     * Called if there was some error with a connection.
     *
     * @param connection the connection with the error.
     * @param errorType type of error, one of a few constants.
     * @param errorDescription description of this error, including any error specific data.
     */
    void onError(Connection connection, String errorType, String errorDescription);
}
