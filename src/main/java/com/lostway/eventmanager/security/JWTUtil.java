package com.lostway.eventmanager.security;

import com.lostway.eventmanager.service.model.UserModel;
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

    public String generateToken(UserModel user) {
        return Jwts.builder()
                .setSubject("User details")
                .setClaims(Map.of("role", user.getRole().name()))
                .setIssuedAt(new Date())
                .setSubject(user.getLogin())
                .setAudience("USERS")
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

            return claims.getBody().getSubject();
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token");
        }
    }
}
