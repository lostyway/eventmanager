package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lostway.eventmanager.enums.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventDto(
        @NotNull Integer id,
        @NotBlank String name,
        @NotBlank String ownerId,
        @NotNull Integer maxPlaces,
        @NotNull Integer occupiedPlaces,
        @NotBlank @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @NotNull @Min(0) Double cost,
        @NotNull @Min(30) Integer duration,
        @NotNull Integer locationId,
        EventStatus status
) {
}
