package com.lostway.eventmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/locations", "/locations/{locationId}").hasAnyRole("USER", "ADMIN")
//                                .requestMatchers(HttpMethod.POST, "/locations").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.PUT, "/locations/{locationId}").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.DELETE, "/locations/{locationId}").hasRole("ADMIN")
//                                .requestMatchers(
//                                        "/swagger-ui/**",
//                                        "/v3/api-docs/**",
//                                        "/swagger-ui.html",
//                                        "/swagger-resources/**",
//                                        "/webjars/**",
//                                        "/openapi.yaml"
//                                ).permitAll()
//                                .anyRequest().authenticated()
//                )
//                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests.anyRequest().permitAll()
                ).build();
    }
}
