package com.lostway.eventmanager.scheduler;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.lostway.eventmanager.enums.EventStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class EventStatusSchedulerTest extends IntegrationTestBase {

    @Autowired
    private EventRepository repository;

    private EventStatusScheduler scheduler;

    private EventEntity event1; // Началось 5 минут назад
    private EventEntity event2; // Уже закончилось 1 минуту назад
    private EventEntity event3; // Еще не началось
    private EventEntity event4; // Началось и уже закончилось

    /**
     * Ненастоящее время. Якобы сейчас 19.07.25. 15:00 по МСК.
     */
    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                ZonedDateTime.of(2025, 7, 19, 15, 0, 0, 0, ZoneId.of("Europe/Moscow")).toInstant(),
                ZoneId.of("Europe/Moscow")
        );
        scheduler = new EventStatusScheduler(repository, clock);

        event1 = EventEntity.builder()
                .name("testEvent")
                .maxPlaces(5)
                .date(LocalDateTime.of(2025, 7, 19, 14, 55))
                .cost(10.0)
                .duration(30)
                .status(WAIT_START)
                .build();
        event1 = repository.save(event1);

        event2 = EventEntity.builder()
                .name("testEvent2")
                .maxPlaces(5)
                .date(LocalDateTime.of(2025, 7, 19, 14, 29))
                .cost(10.0)
                .duration(30)
                .status(WAIT_START)
                .build();
        event2 = repository.save(event2);

        event3 = EventEntity.builder()
                .name("testEvent2")
                .maxPlaces(5)
                .date(LocalDateTime.of(2026, 7, 19, 15, 0))
                .cost(10.0)
                .duration(30)
                .status(WAIT_START)
                .build();
        event3 = repository.save(event3);

        event4 = EventEntity.builder()
                .name("testEvent2")
                .maxPlaces(5)
                .date(LocalDateTime.of(2025, 7, 19, 14, 25))
                .cost(10.0)
                .duration(30)
                .status(STARTED)
                .build();
        event4 = repository.save(event4);
    }

    @Test
    void shouldUpdateStatusToStartedWhenEventHasStarted() {
        scheduler.updateStatus();
        event1 = repository.findById(event1.getId()).get();
        assertThat(event1.getStatus()).isEqualTo(STARTED);
    }

    @Test
    void whenWaitForStartButAlreadyFinished() {
        scheduler.updateStatus();
        event2 = repository.findById(event2.getId()).orElse(null);
        Assertions.assertNotNull(event2);
        assertThat(event2.getStatus()).isEqualTo(FINISHED);
    }

    @Test
    void whenWaitForStartAndNotStarted() {
        scheduler.updateStatus();
        event3 = repository.findById(event3.getId()).orElse(null);
        Assertions.assertNotNull(event3);
        assertThat(event3.getStatus()).isEqualTo(WAIT_START);
    }

    @Test
    void whenStartedAndAlreadyFinished() {
        scheduler.updateStatus();
        event4 = repository.findById(event4.getId()).orElse(null);
        Assertions.assertNotNull(event4);
        assertThat(event4.getStatus()).isEqualTo(FINISHED);
    }

    @Test
    void whenMassiveUpdate() {
        scheduler.updateStatus();
        event1 = repository.findById(event1.getId()).orElse(null);
        Assertions.assertNotNull(event1);
        assertThat(event1.getStatus()).isEqualTo(STARTED);
        event2 = repository.findById(event2.getId()).orElse(null);
        Assertions.assertNotNull(event2);
        assertThat(event2.getStatus()).isEqualTo(FINISHED);
        event3 = repository.findById(event3.getId()).orElse(null);
        Assertions.assertNotNull(event3);
        assertThat(event3.getStatus()).isEqualTo(WAIT_START);
        event4 = repository.findById(event4.getId()).orElse(null);
        Assertions.assertNotNull(event4);
        assertThat(event4.getStatus()).isEqualTo(FINISHED);

    }
}