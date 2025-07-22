package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        @NotBlank String name,
        @NotNull Integer maxPlaces,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @Positive int cost,
        @Min(30) Integer duration,
        @Positive Integer locationId
) {
}
