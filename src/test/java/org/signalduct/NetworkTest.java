package org.signalduct;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.signalduct.MockConnectionListener.ConnectionEvent.*;


public class NetworkTest {

    private static final int PORT = 87654;
    private static final String LOCALHOST = "localhost";
    private static final int TIMEOUT_MILLISECONDS = 1000;

    private MockConnectionListener listenerOnServer;
    private MockConnectionListener listenerOnClient;
    private ClientNetwork clientNetwork;
    private ServerNetwork serverNetwork;

    @Before
    public void setUp() throws Exception {

        listenerOnServer = new MockConnectionListener("listener on server");
        listenerOnClient = new MockConnectionListener("listener on client");
        clientNetwork = new ClientNetwork(listenerOnClient);
        serverNetwork = new ServerNetwork(PORT, listenerOnServer);

        listenerOnServer.assertReceivedNoEvents();
        listenerOnClient.assertReceivedNoEvents();
    }

    @Test
    public void testConnect() throws Exception {

        serverNetwork.startAcceptingConnections();

        Connection connectionToServer = clientNetwork.connectToServer(LOCALHOST, PORT);
        connectionToServer.waitUntilConnected(TIMEOUT_MILLISECONDS);

        listenerOnServer.assertReceivedEventsAre(CONNECTED);
        listenerOnClient.assertReceivedEventsAre(CONNECTED);
        assertTrue(connectionToServer.isConnected());
    }


}
