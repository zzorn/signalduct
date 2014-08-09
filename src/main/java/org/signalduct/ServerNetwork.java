package org.signalduct;

/**
 * Server side network interface.
 */
public interface ServerNetwork extends Network {

    /**
     * Bind to the specified port and listen for incoming connections.
     *
     * @param newConnectionListener handler that is notified when new clients connect.
     *                              It should do any necessary initialization or listener addition for them.
     */
    void bind(int port, NewConnectionListener newConnectionListener);

}
