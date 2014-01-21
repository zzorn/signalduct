package org.signalduct;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.signalduct.MockConnectionListener.ConnectionEvent.*;


public class NetworkTest {

    private static final int PORT = 87654;
    private static final String LOCALHOST = "localhost";
    private MockConnectionListener listenerOnServer;
    private MockConnectionListener listenerOnClient;

    @Before
    public void setUp() throws Exception {
        listenerOnServer = new MockConnectionListener("listener on server");
        listenerOnClient = new MockConnectionListener("listener on client");
    }

    @Test
    public void testConnect() throws Exception {
        ClientNetwork clientNetwork = new ClientNetwork(listenerOnClient);
        ServerNetwork serverNetwork = new ServerNetwork(PORT, listenerOnServer);

        listenerOnServer.assertReceivedNoEvents();
        listenerOnClient.assertReceivedNoEvents();

        serverNetwork.startAcceptingConnections();

        Connection connectionToServer = clientNetwork.connectToServer(LOCALHOST, PORT);
        connectionToServer.waitUntilConnected(1000);

        listenerOnServer.assertReceivedEventsAre(CONNECTED);
        listenerOnClient.assertReceivedEventsAre(CONNECTED);
        assertTrue(connectionToServer.isConnected());
    }


}
