package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.*;
import com.lostway.eventmanager.exception.UserAlreadyExistException;
import com.lostway.eventmanager.mapper.UserMapper;
import com.lostway.eventmanager.security.JWTUtil;
import com.lostway.eventmanager.service.UserService;
import com.lostway.eventmanager.service.model.UserModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAuthenticationController {
    private final UserService userService;
    private final UserMapper mapper;
    private final JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserToShowDto> registerUser(@RequestBody @Valid UserRegistryDto userDto) {
        if (userService.existsByLogin(userDto.getLogin())) {
            throw new UserAlreadyExistException("Пользователь с логином: " + userDto.getLogin() + " уже существует!");
        }

        UserModel registeredUserModel = userService.registerUser(mapper.toModel(userDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toUserToShowDto(registeredUserModel));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponseDto> auth(@RequestBody @Valid UserLoginDto loginDto) {
        String token = userService.auth(mapper.toModel(loginDto));

        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserToShowByIdDto> byId(@PathVariable @NotNull Long userId) {
        UserModel model = userService.getUserById(userId);
        UserToShowByIdDto userToShowDto = mapper.toUserToShowByIdDto(model);
        return ResponseEntity.ok(userToShowDto);
    }
}
