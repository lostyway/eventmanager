package com.lostway.eventmanager.service;

import com.lostway.eventmanager.exception.CapacityNotEnoughException;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.service.model.Event;
import com.lostway.eventmanager.service.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository repository;
    private final LocationService locationService;
    private final EventMapper mapper;
    private final UserService userService;
    private final LocationMapper locationMapper;
    private final EventValidatorService eventValidatorService;

    @Transactional
    @PreAuthorize("hasAuthority('USER')")
    public Event createNewEvent(Event eventToCreate) {
        Location location = locationService.findById(eventToCreate.getLocationId());

        if (eventValidatorService.isLocationPlanned(location)) {
            throw new LocationIsPlannedException("Локация уже занята другим мероприятием");
        }

        if (location.getCapacity() < eventToCreate.getMaxPlaces()) {
            throw new CapacityNotEnoughException("Мест на локации меньше чем предполагается на мероприятии.");
        }

        Long userId = getSecurityUserId();
        eventToCreate.setOwnerId(userId);

        EventEntity savedEntity = repository.save(mapper.toEntity(eventToCreate));
        return mapper.toModel(savedEntity);
    }

    @PreAuthorize("hasAuthority('USER')")
    public List<Event> getUsersEvents() {
        List<EventEntity> eventEntities = repository.findEventByOwnerId(getSecurityUserId());
        return mapper.toModel(eventEntities);
    }

    private Long getSecurityUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByLogin(username).getId();
    }
}
