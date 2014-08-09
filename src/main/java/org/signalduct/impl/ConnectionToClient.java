package org.signalduct.impl;

import org.signalduct.impl.ConnectionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import static org.flowutils.Check.notNull;

/**
 *
 */
public class ConnectionToClient extends ConnectionBase {

    private final SocketChannel socketChannel;

    public ConnectionToClient(SocketChannel socketChannel) {
        notNull(socketChannel, "socketChannel");

        this.socketChannel = socketChannel;
    }

    @Override public boolean isConnected() {
        return socketChannel.isConnected();
    }

    @Override protected SocketChannel getSocketChannel() {
        return socketChannel;
    }

}
