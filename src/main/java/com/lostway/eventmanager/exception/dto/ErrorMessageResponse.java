package com.lostway.eventmanager.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Класс, описывающий ошибку, которая произошла при обращении к контроллеру. Меняется в зависимости от кода ошибки. <p>
 * message <-- Человеко-читаемое сообщение об ошибке<p>
 * detailedMessage <-- Более детальное сообщение об ошибке. Например, содержимое exception<p>
 * dateTime <-- Дата и время возникновения ошибки. Формат "YYYY-MM-DDThh:mm:ss"<p>
 */

public record ErrorMessageResponse(
        String message,
        String detailedMessage,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime
) {
}
