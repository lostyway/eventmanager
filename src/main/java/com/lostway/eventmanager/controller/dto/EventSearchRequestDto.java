package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lostway.eventmanager.enums.EventStatus;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record EventSearchRequestDto(
        String name,
        Integer placesMin,
        Integer placesMax,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateStartAfter,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Integer locationId,
        EventStatus eventStatus
) {
}
