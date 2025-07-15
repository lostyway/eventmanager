package com.lostway.eventmanager.security;

import com.lostway.eventmanager.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.lostway.eventmanager.exception.ErrorMsgResponseFilterExceptionUtil.createJsonError;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final String errorMessage = "Invalid JWT token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && !header.isBlank() && header.startsWith("Bearer ")) {
            String jwt = header.replace("Bearer ", "");

            if (jwt.isBlank()) {
                logger.warn(errorMessage);
                String messageToResponse = createJsonError(response,
                        errorMessage,
                        "Invalid JWT token in Authorization header",
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                response.getWriter().write(messageToResponse);
                return;
            } else {
                try {
                    String username = jwtUtil.validateAndGetUsername(jwt);
                    UserDetails userDetails = userService.findUserDetailsByLogin(username);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities());

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JwtException e) {
                    logger.warn(errorMessage, e);
                    String messageToResponse = createJsonError(response, errorMessage, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(messageToResponse);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
