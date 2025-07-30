package com.lostway.eventmanager.scheduler;

import com.lostway.eventdtos.EventStatus;
import com.lostway.eventdtos.EventStatusChangeKafkaMessage;
import com.lostway.eventmanager.kafka.EventKafkaProducer;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.LocalDateTime.now;


@RequiredArgsConstructor
@Component
public class EventStatusScheduler {
    private final EventRepository repository;
    private final Clock clock;
    private final EventKafkaProducer eventKafkaProducer;

    @Transactional
    @Scheduled(cron = "${scheduler.cron}")
    public void updateStatus() {
        LocalDateTime timeNow = now(clock.withZone(ZoneId.of("Europe/Moscow")));
        List<EventEntity> startedEvents = repository.findByStatusIn(List.of(
                EventStatus.WAIT_START,
                EventStatus.STARTED
        ));


        for (EventEntity event : startedEvents) {
            LocalDateTime start = event.getDate();
            LocalDateTime end = start.plusMinutes(event.getDuration());

            if (timeNow.isAfter(start) && timeNow.isBefore(end) && event.getStatus() == EventStatus.WAIT_START) {
                event.setStatus(EventStatus.STARTED);
                sendStatusMessage(event, EventStatus.STARTED, timeNow);
            } else if (timeNow.isAfter(end) && event.getStatus() != EventStatus.FINISHED) {
                event.setStatus(EventStatus.FINISHED);
                sendStatusMessage(event, EventStatus.FINISHED, timeNow);
            }
        }
    }

    private void sendStatusMessage(EventEntity event, EventStatus eventStatus, LocalDateTime sendTime) {
        eventKafkaProducer.sendStatusEventChanges(
                new EventStatusChangeKafkaMessage()
                        .setEventId(event.getId())
                        .setStatus(eventStatus)
                        .setTimestamp(sendTime)
        );
    }
}