package com.lostway.eventmanager.service;

import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    public boolean isLocationPlanned(LocationEntity locationEntity) {
        return repository.existsEventEntitiesByLocation(locationEntity);
    }
}
