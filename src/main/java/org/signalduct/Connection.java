package org.signalduct;

/**
 *
 */
public interface Connection {

    /**
     * Blocks until the connection is established, or the specified timeout is reached.
     * @param timeoutMilliseconds max number of milliseconds to wait for the connection to complete
     * @throws NetworkException if a connection could not be established within the timeout, or if there was some other problem.
     */
    void waitUntilConnected(int timeoutMilliseconds) throws NetworkException;

    /**
     * @return true if we are currently connected.
     */
    boolean isConnected();
}
