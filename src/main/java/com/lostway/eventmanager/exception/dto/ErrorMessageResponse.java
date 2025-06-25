package com.lostway.eventmanager.exception.dto;

import lombok.*;

/**
 * Класс, описывающий ошибку, которая произошла при обращении к контроллеру. Меняется в зависимости от кода ошибки. <p>
 * message <-- Человеко-читаемое сообщение об ошибке<p>
 * detailedMessage <-- Более детальное сообщение об ошибке. Например, содержимое exception<p>
 * dateTime <-- Дата и время возникновения ошибки. Формат "YYYY-MM-DDThh:mm:ss"<p>
 */

@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageResponse {

    private String message;

    private String detailedMessage;

    private String dateTime;
}
