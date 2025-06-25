package com.lostway.eventmanager.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String address;

    @Min(5)
    private Integer capacity;

    private String description;
}
