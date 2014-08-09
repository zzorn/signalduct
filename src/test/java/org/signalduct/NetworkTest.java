package org.signalduct;

import org.junit.Before;
import org.junit.Test;
import org.signalduct.impl.ClientNetwork;
import org.signalduct.impl.ServerNetwork;

import java.net.InetSocketAddress;

import static org.junit.Assert.*;
import static org.signalduct.MockConnectionListener.ConnectionEvent.*;


public class NetworkTest {

    private static final int PORT = 8765;
    private static final String LOCALHOST = "localhost";
    private static final InetSocketAddress SERVER_LOCALHOST_ADDRESS = new InetSocketAddress(LOCALHOST, PORT);
    private static final int TIMEOUT_MILLISECONDS = 1000;

    private MockConnectionListener listenerOnServer;
    private MockConnectionListener listenerOnClient;
    private ClientNetwork clientNetwork;
    private ServerNetwork serverNetwork;

    @Before
    public void setUp() throws Exception {

        listenerOnServer = new MockConnectionListener("listener on server");
        listenerOnClient = new MockConnectionListener("listener on client");
        clientNetwork = new ClientNetwork();
        serverNetwork = new ServerNetwork(PORT, listenerOnServer);

        listenerOnServer.assertReceivedNoEvents();
        listenerOnClient.assertReceivedNoEvents();
    }

    @Test
    public void testConnect() throws Exception {

        serverNetwork.start();

        Connection connectionToServer = clientNetwork.connectTo(SERVER_LOCALHOST_ADDRESS, listenerOnClient);

        // TODO: Remove sleep:
        Thread.sleep(1000);

        listenerOnServer.assertReceivedEventsAre(CONNECTED);
        listenerOnClient.assertReceivedEventsAre(CONNECTED);
        assertTrue(connectionToServer.isConnected());
    }


}
