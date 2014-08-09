package org.signalduct.impl;

import org.signalduct.Connection;
import org.signalduct.ConnectionListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import static org.flowutils.Check.notNull;

/**
 *
 */
// TODO: Solve logging, create utility methods in flowutils
// TODO: Also create utility method for throwing an error with some exception and message
public class ClientNetwork extends ChannelMonitor<SocketChannel> {

    private ConnectionToServer connectionToServer;

    public Connection connectTo(InetSocketAddress address, ConnectionListener listener) throws NetworkException {
        notNull(address, "address");

        SocketChannel channel = createNonBlockingSocketChannel();
        listenToChannelEvents(channel);
        connectChannelTo(channel, address);

        connectionToServer = new ConnectionToServer(channel);
        connectionToServer.addListener(listener);

        return connectionToServer;
    }

    private void connectChannelTo(SocketChannel socketChannel, InetSocketAddress address) throws NetworkException {
        try {
            socketChannel.connect(address);
        } catch (IOException e) {
            tryToCloseChannel(socketChannel);
            throw new NetworkException("Could not connect to " + address, e);
        }
    }

    private SocketChannel createNonBlockingSocketChannel() throws NetworkException {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            tryToCloseChannel(socketChannel);
            throw new NetworkException("Could not create socket channel", e);
        }
        return socketChannel;
    }

    private void tryToCloseChannel(SocketChannel socketChannel) {
        if (socketChannel != null) {
            try {
                socketChannel.close();
            } catch (IOException e1) {
                // Ignore failure to close
            }
        }
    }

    @Override protected void handleConnectionCreated(SocketChannel channel) throws Exception {


        System.out.println("ClientNetwork.handleConnectionCreated");
    }

    @Override protected void readIncomingData(SocketChannel channel) throws Exception {
        System.out.println("ClientNetwork.readIncomingData");
    }

    @Override protected void writeOutgoingData(SocketChannel channel) throws Exception {
        System.out.println("ClientNetwork.writeOutgoingData");
    }
}
