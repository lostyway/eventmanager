package com.lostway.eventmanager.scheduler;

import com.lostway.eventmanager.enums.EventStatus;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStatusSchedulerTest {

    @Mock
    private EventRepository repository;

    private EventStatusScheduler scheduler;

    @BeforeEach
    void setUp() {
        // 15:00 МСК
        Clock clock = Clock.fixed(
                ZonedDateTime.of(2025, 7, 19, 15, 0, 0, 0, ZoneId.of("Europe/Moscow")).toInstant(),
                ZoneId.of("Europe/Moscow")
        );
        scheduler = new EventStatusScheduler(repository, clock);
    }

    @Test
    void shouldUpdateStatusToStarted_WhenEventHasStarted() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 14, 55)); // началось 5 минут назад
        event.setDuration(20); // закончится в 15:15
        event.setStatus(EventStatus.WAIT_START);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event));

        scheduler.updateStatus();

        assertThat(EventStatus.STARTED).isEqualTo(event.getStatus());
    }

    @Test
    void shouldUpdateStatusToFinished_WhenEventHasEnded() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 14, 0)); // началось в 14:00
        event.setDuration(30); // закончилось в 14:30
        event.setStatus(EventStatus.STARTED);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event));

        scheduler.updateStatus();

        assertThat(EventStatus.FINISHED).isEqualTo(event.getStatus());
    }

    @Test
    void shouldNotUpdate_WhenEventIsInFuture() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 16, 0)); // начнется в будущем
        event.setDuration(30);
        event.setStatus(EventStatus.WAIT_START);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event));

        scheduler.updateStatus();

        assertThat(EventStatus.WAIT_START).isEqualTo(event.getStatus());
    }
}