package com.lostway.eventmanager.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException() {
        super("Мероприятие не было найдено");
    }

    public EventNotFoundException(Number eventId) {
        super("Мероприятие с ID: '%s' не было найдено".formatted(eventId));
    }
}
