package org.signalduct;

import java.net.InetSocketAddress;

/**
 *
 */
public class ClientNetwork {
    public ClientNetwork(ConnectionListener listener) {
        // TODO: Implement
    }

    public Connection startConnectingTo(InetSocketAddress address) throws NetworkException {
        // TODO: Implement
        return new ConnectionToServer();
    }
}
