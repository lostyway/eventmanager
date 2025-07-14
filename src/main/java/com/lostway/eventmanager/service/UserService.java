package com.lostway.eventmanager.service;

import com.lostway.eventmanager.enums.Role;
import com.lostway.eventmanager.mapper.UserMapper;
import com.lostway.eventmanager.repository.UserRepository;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.security.JWTUtil;
import com.lostway.eventmanager.service.model.UserModel;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final JWTUtil jwtUtil;

    @Transactional
    public UserModel registerUser(UserModel model) {
        model.setRole(Role.USER);
        UserEntity entity = mapper.toEntity(model);
        UserEntity saved = userRepository.save(entity);
        return mapper.toModel(saved);
    }

    public UserModel findByLogin(String login) {
        UserEntity userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден по логину"));
        return mapper.toModel(userEntity);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public UserModel getUserById(@NotBlank Long userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден по ID: " + userId));
        return mapper.toModel(entity);
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public UserDetails findUserDetailsByLogin(String login) {
        UserEntity entity = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден по логину"));

        return User.builder()
                .username(entity.getLogin())
                .password(entity.getPassword())
                .roles(entity.getRole().name())
                .build();
    }
}
