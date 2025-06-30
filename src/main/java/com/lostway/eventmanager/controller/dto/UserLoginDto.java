package com.lostway.eventmanager.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDto {

    @Size(min = 3, max = 100, message = "Имя пользователя должно содержать от 3 до 100 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String login;

    @Size(min = 3, max = 255, message = "Длина пароля должна быть от 3 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
