package com.lostway.eventmanager.exception;

public class LocationAlreadyExists extends RuntimeException {
    public LocationAlreadyExists(String message) {
        super(message);
    }
}
