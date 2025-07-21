package com.lostway.eventmanager.service;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.enums.Role;
import com.lostway.eventmanager.repository.LocationRepository;
import com.lostway.eventmanager.repository.UserRepository;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.service.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.lostway.eventmanager.enums.EventStatus.WAIT_START;
import static org.assertj.core.api.Assertions.assertThat;

class EventServiceTest extends IntegrationTestBase {
    @Autowired
    private EventService eventService;
    private UserEntity user;
    private UserEntity admin;
    @Autowired
    private UserRepository userRepository;
    private Event event;
    @Autowired
    private LocationRepository locationRepository;
    private LocationEntity locationEntity;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new UserEntity(null, "test", "pass", 22, Role.USER));
        admin = userRepository.save(new UserEntity(null, "adminLogin", "pass", 22, Role.ADMIN));
        locationEntity = locationRepository.save(new LocationEntity(null, "location", "address", 1000, "desc"));
        event = new Event(
                null,
                "event name",
                10,
                LocalDateTime.of(2025, 12, 11, 0, 0), 100, 0, 30,
                locationEntity.getId(),
                user.getId(),
                WAIT_START);
    }

    @Test
    void whenCreatingEventIsSuccessful() {
        Event created = eventService.createNewEvent(event);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(event.getName());
    }
}