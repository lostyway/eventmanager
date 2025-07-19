package com.lostway.eventmanager.service;

import com.lostway.eventmanager.controller.dto.EventSearchRequestDto;
import com.lostway.eventmanager.enums.EventStatus;
import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.UserEventRegistrationEntityRepository;
import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.repository.entity.UserEventRegistrationEntity;
import com.lostway.eventmanager.service.model.Event;
import com.lostway.eventmanager.service.model.Location;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository repository;
    private final LocationService locationService;
    private final EventMapper mapper;
    private final UserService userService;
    private final EventValidatorService eventValidatorService;
    private final UserEventRegistrationEntityRepository userEventRegistrationEntityRepository;

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

    public void registerNewEvent(@Positive Integer eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventAlreadyStartedException("Регистрация на мероприятие уже закрыто.");
        }

        if (userEventRegistrationEntityRepository.existsByUserIdAndEventId(userEntity.getId(), eventId)) {
            throw new AlreadyRegisteredException("Пользователь уже зарегистрирован на это мероприятие.");
        }

        int placeToBuy = eventEntity.getOccupiedPlaces() + 1;

        if (placeToBuy > eventEntity.getMaxPlaces() || placeToBuy > eventEntity.getLocation().getCapacity()) {
            throw new NotEnoughPlaceException("Недостаточно мест на мероприятии для бронирования");
        }

        UserEventRegistrationEntity registration = new UserEventRegistrationEntity();
        registration.setUser(userEntity);
        registration.setEvent(eventEntity);

        eventEntity.setOccupiedPlaces(placeToBuy);
        userEventRegistrationEntityRepository.save(registration);
        repository.save(eventEntity);
    }

    public void deleteEventRegistration(@Positive Integer eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventAlreadyStartedException("Регистрация на мероприятие уже закрыто. Отменить регистрацию не получится.");
        }

        UserEventRegistrationEntity registrationToDelete = userEventRegistrationEntityRepository.findByUserIdAndEventId(userEntity.getId(), eventId)
                .orElseThrow(UserNotMemberException::new);

        userEventRegistrationEntityRepository.delete(registrationToDelete);

        int occupiedPlaces = Math.max(0, eventEntity.getOccupiedPlaces() - 1);
        eventEntity.setOccupiedPlaces(occupiedPlaces);
        repository.save(eventEntity);
    }

    public List<Event> getUserRegistrationsOnEvents() {
        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());
        List<UserEventRegistrationEntity> registrations = userEventRegistrationEntityRepository.findByUser(userEntity);
        List<EventEntity> list = registrations.stream().map(UserEventRegistrationEntity::getEvent).toList();
        return mapper.toModel(list);
    }

    public Event getEventById(@Positive Integer eventId) {
        EventEntity event = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return mapper.toModel(event);
    }

    public void cancelEventById(@Positive Integer eventId) {
        validateAndGetEventEntity(eventId).setStatus(EventStatus.CANCELLED);
    }

    public Event updateEvent(Integer eventId, Event model) {
        model.setId(eventId);
        EventEntity oldEntity = validateAndGetEventEntity(eventId);
        LocationEntity location = locationService.getLocationFromDb(model.getLocationId());
        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        EventEntity newEntity = mapper.toEntity(model);
        newEntity.setOwner(userEntity);
        newEntity.setLocation(location);

        validateNewEventFields(oldEntity, newEntity);

        EventEntity saved = repository.save(newEntity);
        return mapper.toModel(saved);
    }

    public List<Event> searchEventByFilter(EventSearchRequestDto eventSearchRequestDto) {
        List<EventEntity> entityList = repository.parseAndFindByFilter(eventSearchRequestDto);
        return mapper.toModel(entityList);
    }

    private static void validateNewEventFields(EventEntity oldEntity, EventEntity newEntity) {
        int occupiedPlaces = oldEntity.getOccupiedPlaces();

        if (newEntity.getMaxPlaces() < occupiedPlaces) {
            throw new NotEnoughPlaceException("На новом мероприятии мест меньше, чем записанных гостей.");
        }

        if (newEntity.getLocation().getCapacity() < occupiedPlaces) {
            throw new NotEnoughPlaceException("На новой локации мест меньше, чем записанных гостей.");
        }
    }

    private EventEntity validateAndGetEventEntity(Integer eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!Objects.equals(getSecurityUserId(), eventEntity.getOwner().getId())) {
            throw new AuthorizationDeniedException("Вы не являетесь создателем мероприятия.");
        }

        return eventEntity;
    }
}
