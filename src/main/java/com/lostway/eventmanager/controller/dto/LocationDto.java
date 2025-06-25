package com.lostway.eventmanager.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    private String address;

    @Min(value = 5, message = "Заполненность должна быть больше 5")
    private Integer capacity;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
