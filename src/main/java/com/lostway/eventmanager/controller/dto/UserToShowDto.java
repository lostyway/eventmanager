package com.lostway.eventmanager.controller.dto;

import com.lostway.eventmanager.enums.Role;

public record UserToShowDto(
        Long id,
        String login,
        String password,
        Integer age,
        Role role) {
}
