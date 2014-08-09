package org.signalduct.utils;

import java.lang.reflect.Constructor;

import static org.flowutils.Check.*;
import static org.flowutils.Check.notNull;

public final class ExceptionUtils {

    /**
     * @return a description of the specific exception (that caused some other exception to be thrown),
     *         including both the name of the exception class as well as the exception message.
     *         Formatted with leading colon, designed to be appended after a higher level error message.
     *         If cause is null, returns just a period to be appended to the end of the high level error message.
     */
    public static String getCauseDescription(final Throwable cause) {
        if (cause == null) {
            return ".";
        } else {
            final String causeExceptionName = cause.getClass().getSimpleName();
            final String causeMessage = cause.getMessage();
            return ": " + causeExceptionName + ": " + causeMessage;
        }
    }

    /**
     * Throws an exception of the specified type and with the specified message.
     */
    public static <T extends Throwable> void throwException(Class<T> exceptionType, String message) throws T {
        throwException(exceptionType, message, null);
    }

    /**
     * Throws an exception of the specified type, with the specified message, and the specified cause.
     * Includes the error message of the cause in the exception message.
     */
    public static <T extends Throwable> void throwException(Class<T> exceptionType, String message, Throwable cause) throws T {
        notNull(exceptionType, "exceptionType");
        nonEmptyString(message, "message");

        String composedErrorMessage = message + getCauseDescription(cause);

        // TODO: Log error

        throw createException(exceptionType, composedErrorMessage, cause);
    }

    private static <T extends Throwable> T createException(Class<T> exceptionType, String message, Throwable cause) {
        try {
            if (cause == null) {
                return createExceptionWithoutCause(exceptionType, message);
            }
            else {
                return createExceptionWithCause(exceptionType, message, cause);
            }
        } catch (Throwable e) {
            throw new IllegalStateException("Could not create exception of type " + exceptionType + ", when trying to report error: " + message);
        }
    }

    private static <T extends Throwable> T createExceptionWithoutCause(Class<T> exceptionType, String message) throws Exception {
        final Constructor<T> exceptionConstructor = exceptionType.getConstructor(String.class);
        return exceptionConstructor.newInstance(message);
    }

    private static <T extends Throwable> T createExceptionWithCause(Class<T> exceptionType, String message, Throwable cause) throws Exception {
        final Constructor<T> exceptionConstructor = exceptionType.getConstructor(String.class, Throwable.class);
        return exceptionConstructor.newInstance(message, cause);
    }


    private ExceptionUtils() {
    }
}
