package com.lostway.eventmanager.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JWTUtil(
            @Value("${secret.key}") String secret,
            @Value("${expiration.time}") long expirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject("User details")
                .setClaims(Map.of("username", username))
                .setIssuedAt(new Date())
                .setAudience("USER")
                .setIssuer("event manager")
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(expirationTime).toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().get("username", String.class);
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token");
        }
    }
}
