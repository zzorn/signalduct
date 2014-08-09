package org.signalduct.impl;

import org.signalduct.ConnectionListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.flowutils.Check.notNull;

/**
 *
 */
public class ServerNetwork extends ChannelMonitor<ServerSocketChannel> {

    private final ConnectionListener connectionListener;
    private final AtomicLong nextConnectionId = new AtomicLong(1);

    private final ConcurrentHashMap<Long, ConnectionToClient> connections = new ConcurrentHashMap<Long, ConnectionToClient>();
    private final InetSocketAddress address;

    public ServerNetwork(int port, ConnectionListener connectionListener) {
        notNull(connectionListener, "connectionListener");

        this.connectionListener = connectionListener;

        address = new InetSocketAddress(port);
    }

    @Override public void start() throws NetworkException {
        initializeServerChannel();
        super.start();
    }

    private void initializeServerChannel() throws NetworkException {
        ServerSocketChannel serverChannel = createNonBlockingServerSocketChannel(address);
        listenToChannelEvents(serverChannel);
    }

    private ServerSocketChannel createNonBlockingServerSocketChannel(final InetSocketAddress address) throws NetworkException {
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
            channel.socket().bind(address);

            // Switch to non-blocking mode, allowing us to use selectors to listen to channel events.
            channel.configureBlocking(false);

            return channel;
        } catch (IOException e) {
            // Try to close channel if it was opened
            if (channel != null) try {
                channel.close();
            } catch (IOException e1) {
                // Ignore
            }

            // Throw network exception
            throw new NetworkException("Could not start listening on " + address, e);
        }
    }

    @Override protected void acceptNewConnection(ServerSocketChannel channel) throws Exception {
        SocketChannel channelToNewClient = channel.accept();
        createConnectionToClient(channelToNewClient);
    }

    private void createConnectionToClient(SocketChannel channelToNewClient) {
        ConnectionToClient connectionToClient = new ConnectionToClient(channelToNewClient);
        addConnection(connectionToClient);
        notifyListenersAboutNewConnection(connectionToClient);
    }

    private void addConnection(ConnectionToClient connectionToClient) {
        connections.put(nextConnectionId.getAndIncrement(), connectionToClient);
    }

    private void notifyListenersAboutNewConnection(ConnectionToClient connectionToClient) {
        connectionListener.onConnected(connectionToClient);
    }

}
