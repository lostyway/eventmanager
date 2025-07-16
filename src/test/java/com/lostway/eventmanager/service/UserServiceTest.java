package com.lostway.eventmanager.service;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.enums.Role;
import com.lostway.eventmanager.exception.IncorrectPasswordException;
import com.lostway.eventmanager.mapper.UserMapper;
import com.lostway.eventmanager.security.JWTUtil;
import com.lostway.eventmanager.service.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserMapper mapper;

    private UserModel modelForTest;

    @Autowired
    private JWTUtil jwtUtil;

    @BeforeEach
    void init() {
        modelForTest = new UserModel();
        modelForTest.setId(1L);
        modelForTest.setLogin("lostway");
        modelForTest.setPassword("123");
        modelForTest.setAge(22);
        modelForTest.setRole(Role.ADMIN);
    }

    @Test
    void whenRegisterUserIsSuccessful() {
        String username = modelForTest.getLogin();
        String password = modelForTest.getPassword();

        UserModel result = userService.registerUser(modelForTest);

        assertThat(result.getLogin()).isEqualTo(username);
        assertThat(result.getPassword()).isNotEqualTo(password);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void whenFindByLoginIsSuccessful() {
        UserModel savedFromDb = userService.registerUser(modelForTest);
        Long id = savedFromDb.getId();

        UserModel result = userService.getUserById(id);

        assertThat(result.getLogin()).isEqualTo(savedFromDb.getLogin());
        assertThat(result.getPassword()).isEqualTo(savedFromDb.getPassword());
        assertThat(result.getAge()).isEqualTo(savedFromDb.getAge());
    }

    @Test
    void whenFindByLoginIsFailedByBadId() {
        Long id = -1L;

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь не был найден по ID: " + id);
    }

    @Test
    void whenExistsByLoginIsSuccessful() {
        UserModel savedFromDb = userService.registerUser(modelForTest);

        boolean exists = userService.existsByLogin(savedFromDb.getLogin());
        boolean notExists = userService.existsByLogin("fakeLogin");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void whenFindUserDetailsByLoginIsSuccessful() {
        UserModel savedFromDb = userService.registerUser(modelForTest);

        UserDetails result = userService.findUserDetailsByLogin(savedFromDb.getLogin());

        assertThat(result.getUsername()).isEqualTo(savedFromDb.getLogin());
        assertThat(result.getPassword()).isEqualTo(savedFromDb.getPassword());
        String role = result.getAuthorities().iterator().next().getAuthority();
        assertThat(role).isEqualTo(savedFromDb.getRole().name());
    }

    @Test
    void whenFindUserDetailsByLoginIsFailedByBadId() {
        assertThatThrownBy(() -> userService.findUserDetailsByLogin("fakeLogin"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь не был найден по логину");
    }

    @Test
    void whenAuthIsSuccessful() {
        String password = modelForTest.getPassword();
        userService.registerUser(modelForTest);

        modelForTest.setPassword(password);

        String resultJwt = userService.auth(modelForTest);
        
        assertThat(jwtUtil.validateToken(resultJwt)).isTrue();
        assertThat(jwtUtil.validateAndGetUsername(resultJwt)).isEqualTo(modelForTest.getLogin());
    }

    @Test
    void whenAuthIsFailedByBadLogin() {
        assertThatThrownBy(() -> userService.auth(modelForTest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Пользователь не был найден по логину");
    }

    @Test
    void whenAuthIsFailedByBadPassword() {
        userService.registerUser(modelForTest);
        UserModel newModelWithBadPass = modelForTest;
        newModelWithBadPass.setPassword("badPassword");

        assertThatThrownBy(() -> userService.auth(newModelWithBadPass))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessageContaining("Пароль был введен неверно!");
    }
}
