package org.signalduct;

/**
 *
 */
public interface ConnectionListener {

    void onMessage(Connection connection, Object Message);

    void onConnected(Connection connection);

    void onDisconnected(Connection connection);

    void onError(Connection connection, String errorType, String errorDescription);

}
