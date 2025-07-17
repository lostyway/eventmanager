package com.lostway.eventmanager.service;

import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.service.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventValidatorService {
    private final EventRepository repository;
    private final LocationMapper mapper;

    boolean isLocationPlanned(Location location) {
        return repository.existsEventEntitiesByLocation(mapper.toEntity(location));
    }
}
