package org.signalduct;

import org.signalduct.filter.LowerLevel;

/**
 * ServerNetwork implementation that uses Java new IO functions.
 */
public class NioServerNetwork extends NetworkBase implements ServerNetwork {

    @Override public void bind(int port, NewConnectionListener newConnectionListener) {
        // TODO: Implement


        initialize(new LowerLevel() {
            @Override public void sendMessage(Connection connection, Object message) {
                // TODO: Implement

            }

            @Override public void sendDisconnect(Connection connection) {
                // TODO: Implement

            }
        }, new ConnectionListener() {
                       @Override public void onMessage(Connection connection, Object message) {
                           // TODO: Implement

                       }

                       @Override public void onConnected(Connection connection) {
                           // TODO: Implement

                       }

                       @Override public void onDisconnected(Connection connection) {
                           // TODO: Implement

                       }
                   });
    }

    @Override public void stop() {

        // TODO


        deInitialize();
    }
}
