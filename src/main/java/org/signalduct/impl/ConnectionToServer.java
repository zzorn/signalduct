package org.signalduct.impl;

import org.flowutils.Check;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import static org.flowutils.Check.notNull;

/**
 * Connection from a client to a server.
 */
public class ConnectionToServer extends ConnectionBase {

    private final SocketChannel socketChannel;

    public ConnectionToServer(SocketChannel socketChannel) {
        Check.notNull(socketChannel, "socketChannel");

        this.socketChannel = socketChannel;
    }

    /**
     * Closes the network connection, if it is open.
     * @throws NetworkException thrown if there was some problem in closing the connection.
     */
    public void close() throws NetworkException {
        try {
            socketChannel.close();
        } catch (IOException e) {
            throw new NetworkException("Could not close connection", e);
        }
    }



    @Override public boolean isConnected() {
        return socketChannel.isConnected();
    }

    @Override protected SocketChannel getSocketChannel() {
        return socketChannel;
    }

}
