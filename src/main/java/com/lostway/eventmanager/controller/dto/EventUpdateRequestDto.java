package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        String name,
        Integer maxPlaces,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @PositiveOrZero int cost,
        @Min(30) Integer duration,
        Integer locationId
) {
}
