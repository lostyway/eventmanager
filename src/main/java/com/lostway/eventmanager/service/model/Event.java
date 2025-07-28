package com.lostway.eventmanager.service.model;

import com.lostway.eventmanager.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long id;

    private String name;

    private Integer maxPlaces;

    private LocalDateTime date;

    private Double cost;

    private int occupiedPlaces;

    private Integer duration;

    private Long locationId;

    private Long ownerId;

    private EventStatus status = EventStatus.WAIT_START;
}
