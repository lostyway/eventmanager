package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.EventCreateRequestDto;
import com.lostway.eventmanager.controller.dto.EventDto;
import com.lostway.eventmanager.controller.dto.EventSearchRequestDto;
import com.lostway.eventmanager.controller.dto.EventUpdateRequestDto;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.service.EventService;
import com.lostway.eventmanager.service.model.Event;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper mapper;

    @PostMapping
    public ResponseEntity<EventDto> createNewEvent(@RequestBody @Valid EventCreateRequestDto eventCreateRequestDto) {
        Event newEventCreated = eventService.createNewEvent(mapper.toModel(eventCreateRequestDto));
        EventDto result = mapper.toDto(newEventCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvents() {
        List<Event> eventsModels = eventService.getUsersEvents();
        List<EventDto> events = mapper.toDto(eventsModels);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/registrations/{eventId}")
    public ResponseEntity<String> registerNewEvent(@PathVariable @Positive Long eventId) {
        eventService.registerNewEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body("Успешная регистрация на мероприятие");
    }

    @DeleteMapping("/registrations/cancel/{eventId}")
    public ResponseEntity<String> cancelEvent(@PathVariable @Positive Long eventId) {
        eventService.deleteEventRegistration(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventDto>> getUserRegistrations() {
        List<Event> events = eventService.getUserRegistrationsOnEvents();
        return ResponseEntity.ok(mapper.toDto(events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable @Positive Long eventId) {
        Event event = eventService.getEventById(eventId);
        EventDto result = mapper.toDto(event);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable @Positive Long eventId) {
        eventService.cancelEventById(eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @RequestBody @Valid EventUpdateRequestDto eventUpdateRequestDto,
            @PathVariable @Positive Long eventId
    ) {
        Event newEvent = eventService.updateEvent(eventId, mapper.toModel(eventUpdateRequestDto));
        EventDto result = mapper.toDto(newEvent);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvent(@RequestBody @Valid EventSearchRequestDto eventSearchRequestDto) {
        List<Event> eventList = eventService.searchEventByFilter(eventSearchRequestDto);
        List<EventDto> result = mapper.toDto(eventList);
        return ResponseEntity.ok(result);
    }
}
