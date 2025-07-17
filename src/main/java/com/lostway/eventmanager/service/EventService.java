package com.lostway.eventmanager.service;

import com.lostway.eventmanager.enums.EventStatus;
import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.UserEventRegistrationEntityRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.repository.entity.UserEventRegistrationEntity;
import com.lostway.eventmanager.service.model.Event;
import com.lostway.eventmanager.service.model.Location;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
    private final EventValidatorService eventValidatorService;
    private final UserEventRegistrationEntityRepository userEventRegistrationEntityRepository;

    @Transactional
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

    public List<Event> getUsersEvents() {
        List<EventEntity> eventEntities = repository.findEventByOwnerId(getSecurityUserId());
        return mapper.toModel(eventEntities);
    }

    private Long getSecurityUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByLogin(username).getId();
    }

    @Transactional
    public void registerNewEvent(@Positive Integer eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие с ID: '%s' не было найдено.".formatted(eventId)));

        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventAlreadyStartedException("Регистрация на мероприятие уже закрыто.");
        }

        if (userEventRegistrationEntityRepository.existsByUserIdAndEventId(userEntity.getId(), eventId)) {
            throw new AlreadyRegisteredException("Пользователь уже зарегистрирован на это мероприятие.");
        }

        UserEventRegistrationEntity registration = new UserEventRegistrationEntity();
        registration.setUser(userEntity);
        registration.setEvent(eventEntity);
        userEventRegistrationEntityRepository.save(registration);

        eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces() + 1);
        repository.save(eventEntity);
    }
}
