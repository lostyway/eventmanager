package com.lostway.eventmanager.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank String name,
        @NotNull Integer maxPlaces,
        @NotBlank @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime date,
        @NotNull Integer cost,
        @NotNull @Min(30) Integer duration,
        @NotNull Integer locationId
) {
}
