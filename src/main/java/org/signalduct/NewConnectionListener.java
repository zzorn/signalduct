package org.signalduct;

/**
 * Listener that is notified about new incoming connections.
 */
public interface NewConnectionListener {

    /**
     * Called when a new connection is formed.
     * The listener can add necessary listeners to the connection, or handle it in some other way.
     */
    void onNewConnection(Connection connection);

}
