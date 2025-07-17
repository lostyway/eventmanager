package com.lostway.eventmanager.exception;

public class UserNotMemberException extends RuntimeException {
    public UserNotMemberException(String message) {
        super(message);
    }
}
