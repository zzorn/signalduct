package org.signalduct;

import org.flowutils.Strings;
import org.junit.Assert;

import java.util.Collections;
import java.util.EnumSet;

import static org.signalduct.MockConnectionListener.ConnectionEvent.*;

/**
 * ConnectionListener implementation that remembers what events it has received since the last call to reset.
 * Used for testing purposes.
 */
public class MockConnectionListener implements ConnectionListener {

    private final String listenerName;

    private EnumSet<ConnectionEvent> receivedEvents = EnumSet.noneOf(ConnectionEvent.class);
    private Object message;
    private String errorType;
    private String errorMessage;

    /**
     * Enum used to describe the different events that a ConnectionListener can receive.
     */
    public static enum ConnectionEvent {
        CONNECTED,
        DISCONNECTED,
        MESSAGE,
        ERROR;

        public String getDisplayName() {
            return name();
        }
    }


    public MockConnectionListener(String listenerName) {
        this.listenerName = listenerName;

        reset();
    }

    /**
     * Clears all received events, sets received objects to null.
     */
    public void reset() {
        receivedEvents.clear();
        message = null;
        errorType = null;
        errorMessage = null;
    }

    /**
     * Assert that we have received a message event with the specified message.
     */
    public void assertMessage(Object expectedMessage) {
        assertReceivedEventsContain(MESSAGE);
        Assert.assertEquals("Message should be correct in " + listenerName, expectedMessage, message);
    }

    /**
     * Assert that we have received an error event with the specified error type.
     */
    public void assertError(String expectedErrorType) {
        assertReceivedEventsContain(ERROR);
        Assert.assertEquals("Error type should be correct in " + listenerName, expectedErrorType, errorType);
    }

    /**
     * Assert that we have received no events so far.
     */
    public void assertReceivedNoEvents() {
        assertReceivedEventsAre();
    }

    /**
     * Assert that we have received all of, and only, the specified events.
     */
    public void assertReceivedEventsAre(ConnectionEvent... expectedEvents) {
        final EnumSet<ConnectionEvent> expectedEventSet = createSet(expectedEvents);

        for (ConnectionEvent event : values()) {
            final boolean eventExpected = expectedEventSet.contains(event);
            assertEventAsExpected(event, eventExpected);
        }
    }

    /**
     * Assert that we have received all of the specified events.  Other events may also have been received.
     */
    public void assertReceivedEventsContain(ConnectionEvent... requiredEvents) {
        final EnumSet<ConnectionEvent> requiredEventsSet = createSet(requiredEvents);

        for (ConnectionEvent requiredEvent : requiredEventsSet) {
            assertEventAsExpected(requiredEvent, true);
        }
    }

    private EnumSet<ConnectionEvent> createSet(ConnectionEvent[] expectedEvents) {
        EnumSet<ConnectionEvent> expectedState = EnumSet.noneOf(ConnectionEvent.class);
        Collections.addAll(expectedState, expectedEvents);
        return expectedState;
    }

    private void assertEventAsExpected(ConnectionEvent event, boolean expectedState) {

        final String message = listenerName + " should "+(expectedState ? "" : "not ")+"have received a " + event.getDisplayName() + " event";

        final boolean actualState = receivedEvents.contains(event);

        Assert.assertTrue(message, expectedState == actualState);
    }

    @Override public void onConnected(Connection connection) {
        receivedEvents.add(CONNECTED);
    }

    @Override public void onDisconnected(Connection connection) {
        receivedEvents.add(DISCONNECTED);
    }

    @Override public void onMessage(Connection connection, Object message) {
        receivedEvents.add(MESSAGE);
        this.message = message;
    }

    @Override public void onError(Connection connection, String errorType, String errorMessage) {
        receivedEvents.add(ERROR);
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }


}