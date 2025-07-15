package com.lostway.eventmanager.security;

import com.lostway.eventmanager.enums.Role;
import com.lostway.eventmanager.repository.UserRepository;
import com.lostway.eventmanager.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminUser() {
        String adminLogin = "admin";
        String rawPassword = "admin";

        boolean exists = userRepository.existsByLogin(adminLogin);
        if (exists) {
            return;
        }

        UserEntity admin = UserEntity.builder()
                .login(adminLogin)
                .password(passwordEncoder.encode(rawPassword))
                .age(22)
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Админ создан: login=admin, password=admin");
    }
}
