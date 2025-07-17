package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank String name,
        @NotNull @Positive Integer maxPlaces,
        @Future @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @PositiveOrZero Integer cost,
        @NotNull @Min(30) Integer duration,
        @NotNull @Min(1) Integer locationId
) {
}
