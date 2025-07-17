package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        String name,
        Integer maxPlaces,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @Min(0) Double cost,
        @Min(30) Integer duration,
        Integer locationId
) {
}
