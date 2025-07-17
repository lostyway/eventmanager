package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.EventCreateRequestDto;
import com.lostway.eventmanager.controller.dto.EventDto;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.service.EventService;
import com.lostway.eventmanager.service.model.Event;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
