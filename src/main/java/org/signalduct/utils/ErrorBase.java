package org.signalduct.utils;

/**
 * A base class for errors, that creates clearer error messages by describing the cause in more detail tha Java does by default.
 */
public abstract class ErrorBase extends Error {

    public ErrorBase() {
    }

    public ErrorBase(String message) {
        super(message);
    }

    public ErrorBase(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorBase(Throwable cause) {
        super(cause);
    }

    public ErrorBase(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override public String getMessage() {
        return super.getMessage() + ExceptionUtils.getCauseDescription(getCause());
    }

}
