package com.lostway.eventmanager.service;

import com.lostway.eventmanager.mapper.UserMapper;
import com.lostway.eventmanager.repository.UserRepository;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.service.model.UserModel;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserModel findByLogin(String login) {
        UserEntity userEntity = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден по логину"));
        return mapper.toModel(userEntity);
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public UserModel registerUser(UserModel model) {
        //TODO Логика регистрации
        return null;
    }

    public UserModel getUserById(@NotBlank Long userId) {
        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не был найден по ID: " + userId));
        return mapper.toModel(entity);
    }
}
