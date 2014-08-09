package org.signalduct.filter;

import org.signalduct.Connection;
import org.signalduct.ConnectionListener;

/**
 * Filter incoming and outgoing messages in some way.
 */
public interface MessageFilter {

    /**
     * Called when a message is received from the lower level network side.
     *
     * @param connection connection that the message is received from.
     * @param message received message.
     * @param nextFilter the next higher level to forward a processed message to.
     * @param previousFilter if some replies to lower levels are needed, this can be used to send messages.
     */
    void receiveMessage(Connection connection, Object message, ConnectionListener nextFilter, LowerLevel previousFilter);

    /**
     * Called when a message is received from the higher level application side.
     *
     * @param connection connection that the message is to be sent with.
     * @param message message to send
     * @param nextFilter the next lower level to forward a processed message to.
     * @param previousFilter if some replies to the higher levels are needed, this can be used to send messages.
     */
    void sendMessage(Connection connection, Object message, LowerLevel nextFilter, ConnectionListener previousFilter);

    /**
     * Called when a connection is received.
     *
     * @param connection connection that was connected.
     * @param nextFilter next filter in the chain to send the onConnected message to.
     * @param previousFilter if some replies to lower levels are needed, this can be used to send messages.
     */
    void receiveConnect(Connection connection, ConnectionListener nextFilter, LowerLevel previousFilter);

    /**
     * Called when a disconnect is received.
     *
     * @param connection connection that was disconnected.
     * @param nextFilter the next higher level to forward the disconnect to
     */
    void receiveDisconnect(Connection connection, ConnectionListener nextFilter);

    /**
     * Called when a disconnect is sent.
     *
     * @param connection connection that should be disconnected.
     * @param nextFilter the next lower level to forward the disconnect to
     */
    void sendDisconnect(Connection connection, LowerLevel nextFilter);

}
