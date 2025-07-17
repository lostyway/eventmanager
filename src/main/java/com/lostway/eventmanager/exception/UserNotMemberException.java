package com.lostway.eventmanager.exception;

public class UserNotMemberException extends RuntimeException {

    public UserNotMemberException() {
        super("Пользователь не является участником мероприятия.");
    }
}
