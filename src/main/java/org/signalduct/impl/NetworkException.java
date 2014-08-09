package org.signalduct.impl;

import org.signalduct.utils.ExceptionBase;

/**
 * Indicates that there was some problem with the network.
 */
public class NetworkException extends ExceptionBase {

    public NetworkException() {
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
