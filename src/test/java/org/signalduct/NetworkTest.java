package org.signalduct;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.signalduct.MockConnectionListener.ConnectionEvent.*;


public class NetworkTest {

    private static final int PORT = 87654;
    private static final String LOCALHOST = "localhost";
    private MockConnectionListener serverListener;
    private MockConnectionListener clientListener;

    @Before
    public void setUp() throws Exception {
        serverListener = new MockConnectionListener();
        clientListener = new MockConnectionListener();
    }

    @Test
    public void testConnect() throws Exception {
        ClientNetwork clientNetwork = new ClientNetwork(clientListener);
        ServerNetwork serverNetwork = new ServerNetwork(PORT, serverListener);

        serverListener.assertReceivedNoEvents();
        clientListener.assertReceivedNoEvents();

        serverNetwork.startAcceptingConnections();

        Connection connection = clientNetwork.connect(LOCALHOST, PORT);
        connection.waitUntilConnected(1000);

        serverListener.assertReceivedEventsAre(CONNECTED);
        clientListener.assertReceivedEventsAre(CONNECTED);
        assertTrue(connection.isConnected());
    }


}
