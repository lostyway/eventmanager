package com.lostway.eventmanager.repository.entity;

import com.lostway.eventmanager.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String login;

    @Min(0)
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;
}
