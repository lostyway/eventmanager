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
        Clock clock = Clock.fixed(
                ZonedDateTime.of(2025, 7, 19, 15, 0, 0, 0, ZoneId.of("Europe/Moscow")).toInstant(),
                ZoneId.of("Europe/Moscow")
        );
        scheduler = new EventStatusScheduler(repository, clock);
    }

    @Test
    void shouldUpdateStatusToStarted_WhenEventHasStarted() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 14, 55));
        event.setDuration(20);
        event.setStatus(EventStatus.WAIT_START);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event));

        scheduler.updateStatus();

        assertThat(EventStatus.STARTED).isEqualTo(event.getStatus());
    }

    @Test
    void shouldUpdateStatusToFinished_WhenEventHasEnded() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 14, 0));
        event.setDuration(30);
        event.setStatus(EventStatus.STARTED);
        EventEntity event2 = new EventEntity();
        event2.setDate(LocalDateTime.of(2025, 7, 19, 14, 0));
        event2.setDuration(30);
        event2.setStatus(EventStatus.STARTED);
        EventEntity event3 = new EventEntity();
        event3.setDate(LocalDateTime.of(2025, 7, 19, 14, 0));
        event3.setDuration(30);
        event3.setStatus(EventStatus.STARTED);
        EventEntity event4 = new EventEntity();
        event4.setDate(LocalDateTime.of(2025, 7, 19, 14, 0));
        event4.setDuration(30);
        event4.setStatus(EventStatus.STARTED);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event, event2, event3, event4));

        scheduler.updateStatus();

        assertThat(EventStatus.FINISHED).isEqualTo(event.getStatus());
        assertThat(EventStatus.FINISHED).isEqualTo(event2.getStatus());
        assertThat(EventStatus.FINISHED).isEqualTo(event3.getStatus());
        assertThat(EventStatus.FINISHED).isEqualTo(event4.getStatus());
    }

    @Test
    void shouldNotUpdate_WhenEventIsInFuture() {
        EventEntity event = new EventEntity();
        event.setDate(LocalDateTime.of(2025, 7, 19, 16, 0));
        event.setDuration(30);
        event.setStatus(EventStatus.WAIT_START);

        when(repository.findByStatusIn(anyList())).thenReturn(List.of(event));

        scheduler.updateStatus();

        assertThat(EventStatus.WAIT_START).isEqualTo(event.getStatus());
    }
}