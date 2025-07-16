package com.lostway.eventmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.lostway.eventmanager.exception.ErrorDtoBuilderUtil.createJsonError;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        log.error("Handling authentication error", authException);
        String stringResponse = createJsonError(response,
                "Необходима аутентификация",
                authException.getLocalizedMessage(),
                HttpServletResponse.SC_FORBIDDEN
        );
        response.getWriter().write(stringResponse);
    }
}
