package com.lostway.eventmanager.service.model;

import com.lostway.eventmanager.enums.Role;
import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String login;
    private String password;
    private Integer age;
    private Role role = Role.USER;
}
