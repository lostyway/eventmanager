package com.lostway.eventmanager.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistryDto {

    @Size(min = 3, max = 100, message = "Имя пользователя должно содержать от 3 до 100 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String login;

    @Size(min = 3, max = 255, message = "Длина пароля должна быть от 3 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @Min(value = 18, message = "Ваш возраст должен быть больше 18 лет")
    @Max(value = 150, message = "Максимально допустимый возраст: 150 лет")
    private Integer age;
}
