package org.signalduct;

import java.net.InetAddress;

/**
 * Client side network interface.
 */
public interface ClientNetwork extends Network {

    /**
     * Connect to the specified server.
     * @return connection object that can be used to add listeners and send messages.
     */
    Connection connect(InetAddress serverAddress);

    /**
     * Connect to the specified server.
     * @param listener a listener that is added to the connection and notified about incoming messages.
     * @return connection object that can be used to add listeners and send messages.
     */
    Connection connect(InetAddress serverAddress, ConnectionListener listener);


}
