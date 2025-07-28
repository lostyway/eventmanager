package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lostway.eventmanager.enums.EventStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventDto(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank Long ownerId,
        @NotNull Integer maxPlaces,
        @NotNull Integer occupiedPlaces,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @NotNull @Positive int cost,
        @NotNull @Min(30) Integer duration,
        @NotNull Long locationId,
        EventStatus status
) {
}
