package org.signalduct.utils;

/**
 * A base class for exceptions, that creates clearer error messages by describing the cause in more detail than Java does by default.
 */
public abstract class ExceptionBase extends Exception {

    public ExceptionBase() {
    }

    public ExceptionBase(String message) {
        super(message);
    }

    public ExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionBase(Throwable cause) {
        super(cause);
    }

    public ExceptionBase(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override public String getMessage() {
        return super.getMessage() + ExceptionUtils.getCauseDescription(getCause());
    }

}
