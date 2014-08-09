package org.signalduct.impl;

import org.flowutils.Check;
import org.signalduct.Connection;
import org.signalduct.ConnectionListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.flowutils.Check.*;
import static org.flowutils.Check.notNull;

/**
 *
 */
public abstract class ConnectionBase implements Connection {

    private static final Comparator<ConnectionListener> HASH_CODE_COMPARATOR = new Comparator<ConnectionListener>() {
        @Override public int compare(ConnectionListener o1, ConnectionListener o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };

    private final Set<ConnectionListener> listeners = new ConcurrentSkipListSet<ConnectionListener>(HASH_CODE_COMPARATOR);
    private boolean connected = false;

    protected ConnectionBase() {
        startUpdateThread(10);
    }

    @Override public final void waitUntilConnected(int timeoutMilliseconds) throws NetworkException {
        positive(timeoutMilliseconds, "timeoutMilliseconds");

        // TODO: Extract timeout waiter class or function
        boolean interrupted = false;
        final long timeoutTime = System.currentTimeMillis() + timeoutMilliseconds;
        try {
            while((getSocketChannel() == null || !getSocketChannel().finishConnect()) &&
                  System.currentTimeMillis() < timeoutTime
                  && !interrupted) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }

            if (interrupted) throw new NetworkException("Interrupted while waiting to connect");
            else if (getSocketChannel() == null || !getSocketChannel().finishConnect()) throw new NetworkException("Timeout while waiting to connect");
        } catch (IOException e) {
            throw new NetworkException("Problem when waiting for connection to complete", e);
        }
    }

    @Override public void update() {
        final SocketChannel socketChannel = getSocketChannel();
        if (socketChannel != null && socketChannel.isConnected())  {
            // Check if we got connected
            if (!connected) {
                connected = true;
                notifyConnected();
            }

            // Read data
            // TODO
            // socketChannel.read(..)

            // Write data
            // TODO
            // socketChannel.write(..)

        }
        else {
            // Check if we got disconnected
            if (connected) {
                connected = false;
                notifyDisconnected();
            }
        }
    }

    @Override public void startUpdateThread(final long updateIntervalMilliseconds) {
        final Thread updateThread = new Thread(new Runnable() {
            @Override public void run() {
                while (true) {
                    update();

                    try {
                        Thread.sleep(updateIntervalMilliseconds);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public final void addListener(ConnectionListener listener) {
        notNull(listener, "listener");

        listeners.add(listener);
    }

    public final void removeListener(ConnectionListener listener) {
        notNull(listener, "listener");

        listeners.remove(listener);
    }

    protected final void notifyConnected() {
        for (ConnectionListener listener : listeners) {
            listener.onConnected(this);
        }
    }

    protected final void notifyDisconnected() {
        for (ConnectionListener listener : listeners) {
            listener.onDisconnected(this);
        }
    }

    protected final void notifyMessage(Object message) {
        for (ConnectionListener listener : listeners) {
            listener.onMessage(this, message);
        }
    }

    protected final void notifyError(String errorType, String errorDescription) {
        for (ConnectionListener listener : listeners) {
            listener.onError(this, errorType, errorDescription);
        }
    }

    protected abstract SocketChannel getSocketChannel();


}
