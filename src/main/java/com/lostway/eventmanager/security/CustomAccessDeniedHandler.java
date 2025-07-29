package com.lostway.eventmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.lostway.eventmanager.exception.ErrorDtoBuilderUtil.createJsonError;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        log.error("Handling authentication error", accessDeniedException);
        String stringResponse = createJsonError(response,
                "Недостаточно прав для выполнения операции",
                accessDeniedException.getMessage(),
                HttpServletResponse.SC_FORBIDDEN
        );
        response.getWriter().write(stringResponse);
    }
}
