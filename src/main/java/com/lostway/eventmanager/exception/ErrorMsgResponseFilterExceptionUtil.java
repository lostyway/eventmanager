package com.lostway.eventmanager.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lostway.eventmanager.exception.dto.ErrorMessageResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ErrorMsgResponseFilterExceptionUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Устанавливает response под JSON и возвращает ErrorMessageResponse в виде JSON для вывода ошибок в фильтрах
     */
    
    @SneakyThrows
    public static String createJsonError(HttpServletResponse response, String message, String detailedMessage, int status) {
        try {
            ErrorMessageResponse invalidJwtToken = new ErrorMessageResponse(
                    message,
                    detailedMessage,
                    LocalDateTime.now());
            String responseMessage = MAPPER.writeValueAsString(invalidJwtToken);
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            return responseMessage;
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибке при парсинге ошибки в JSON");
            throw e;
        }
    }
}
