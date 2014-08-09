package org.signalduct.impl;

import org.signalduct.Connection;
import org.signalduct.ConnectionListener;

/**
 *
 */
public abstract class ConnectionListenerAdapter implements ConnectionListener {
    @Override public void onMessage(Connection connection, Object Message) {
    }

    @Override public void onConnected(Connection connection) {
    }

    @Override public void onDisconnected(Connection connection) {
    }

    @Override public void onError(Connection connection, String errorType, String errorDescription) {
    }
}
