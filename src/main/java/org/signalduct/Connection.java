package org.signalduct;


/**
 * Represents a network connection between a client and server.
 */
public interface Connection {

    /**
     * @param listener listener that is notified about received messages and status changes.
     */
    void addListener(ConnectionListener listener);

    /**
     * @param listener listener to remove
     */
    void removeListener(ConnectionListener listener);


    /**
     * Send a message object to the connected network.
     */
    void sendMessage(Object message);


    /**
     * Stop the connection.
     */
    void disconnect();


    /**
     * Store some custom data with the connection.
     */
    void setCustomData(String name, Object data);

    /**
     * @return previously stored custom data.  null if not found.
     */
    <T> T getCustomData(String name);

    /**
     * @return previously stored custom data.  defaultValue if not found.
     */
    <T> T getCustomData(String name, T defaultValue);

}
