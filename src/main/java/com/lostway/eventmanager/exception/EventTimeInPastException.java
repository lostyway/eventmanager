package com.lostway.eventmanager.exception;

public class EventTimeInPastException extends RuntimeException {
    public EventTimeInPastException(String message) {
        super(message);
    }
}
