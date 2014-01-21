package org.signalduct;

/**
 *
 */
public interface Connection {


    void waitUntilConnected(int timeoutMilliseconds);

    /**
     * @return true if we are currently connected.
     */
    boolean isConnected();
}
