package com.lostway.eventmanager.scheduler;

import com.lostway.eventmanager.enums.EventStatus;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


@RequiredArgsConstructor
@Component
public class EventStatusScheduler {
    private final EventRepository repository;
    private final Clock clock;

    @Scheduled(cron = "${scheduler.cron}")
    public void updateStatus() {
        List<EventEntity> startedEvents = repository.findByStatusIn(List.of(
                EventStatus.WAIT_START,
                EventStatus.STARTED
        ));

        LocalDateTime now = LocalDateTime.now(clock.withZone(ZoneId.of("Europe/Moscow")));

        for (EventEntity event : startedEvents) {
            LocalDateTime start = event.getDate();
            LocalDateTime end = start.plusMinutes(event.getDuration());

            if (now.isAfter(start) && now.isBefore(end) && event.getStatus() == EventStatus.WAIT_START) {
                event.setStatus(EventStatus.STARTED);
            } else if (now.isAfter(end) && event.getStatus() != EventStatus.FINISHED) {
                event.setStatus(EventStatus.FINISHED);
            }
        }
    }
}