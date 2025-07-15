package com.lostway.eventmanager.controller.dto;

import com.lostway.eventmanager.enums.Role;

public record UserToShowByIdDto(Long id,
                                String login,
                                Integer age,
                                Role role) {
}
