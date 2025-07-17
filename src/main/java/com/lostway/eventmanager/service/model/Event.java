package com.lostway.eventmanager.service.model;

import com.lostway.eventmanager.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
    private Integer id;

    private String name;

    private Integer maxPlaces;

    private LocalDateTime date;

    private Integer cost;

    private int occupiedPlaces;

    private Integer duration;

    private Integer locationId;

    private Long ownerId;

    private EventStatus status = EventStatus.WAIT_START;
}
