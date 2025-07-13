package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.UserLoginDto;
import com.lostway.eventmanager.controller.dto.UserRegistryDto;
import com.lostway.eventmanager.controller.dto.UserToShowDto;
import com.lostway.eventmanager.exception.UserAlreadyExistException;
import com.lostway.eventmanager.mapper.UserMapper;
import com.lostway.eventmanager.security.JwtAuthenticationResponse;
import com.lostway.eventmanager.service.UserService;
import com.lostway.eventmanager.service.model.UserModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAuthenticationController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserToShowDto registerUser(@RequestBody @Valid UserRegistryDto userDto) {
        if (userService.existsByLogin(userDto.getLogin())) {
            throw new UserAlreadyExistException("Пользователь с логином: " + userDto.getLogin() + " уже существует!");
        }

        //TODO Процесс регистрации

        UserModel registeredUserModel = userService.registerUser(mapper.toModel(userDto));
        return mapper.toUserToShowDto(registeredUserModel);
    }

    @PostMapping("/auth")
    public JwtAuthenticationResponse authenticateUser(@RequestBody @Valid UserLoginDto userToLoginDto) {
        return null;
    }

    @GetMapping("/{userId}")
    public UserToShowDto byId(@PathVariable @NotBlank Long userId) {
        UserModel model = userService.getUserById(userId);
        return mapper.toUserToShowDto(model);
    }
}
