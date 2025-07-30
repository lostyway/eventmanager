package com.lostway.eventmanager.service;

import com.lostway.eventdtos.EventStatus;
import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.service.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventValidatorService {
    private final EventRepository repository;
    private final LocationMapper mapper;

    boolean isLocationPlanned(Location location) {
        return repository.existsEventEntitiesByLocationAndStatusIn(mapper.toEntity(location), List.of(EventStatus.STARTED, EventStatus.WAIT_START));
    }
}