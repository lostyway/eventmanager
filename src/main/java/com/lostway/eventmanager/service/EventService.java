package com.lostway.eventmanager.service;

import com.lostway.eventdtos.EventChangeKafkaMessage;
import com.lostway.eventdtos.EventStatus;
import com.lostway.eventdtos.FieldChange;
import com.lostway.eventmanager.controller.dto.EventSearchRequestDto;
import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.kafka.EventKafkaProducer;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.mapper.LocationMapper;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final Clock clock = Clock.system(ZoneId.of("Europe/Moscow"));
    private final EventRepository repository;
    private final LocationService locationService;
    private final EventMapper mapper;
    private final UserService userService;
    private final UserEventRegistrationEntityRepository userEventRegistrationEntityRepository;
    private final EventKafkaProducer eventKafkaProducer;
    private final LocationMapper locationMapper;

    public Event createNewEvent(Event eventToCreate) {
        Location location = locationService.findById(eventToCreate.getLocationId());
        isLocationPlanned(eventToCreate, location);

        if (location.getCapacity() < eventToCreate.getMaxPlaces()) {
            throw new CapacityNotEnoughException("Мест на локации меньше чем предполагается на мероприятии.");
        }

        Long userId = getSecurityUserId();
        eventToCreate.setOwnerId(userId);
        eventToCreate.setStatus(EventStatus.WAIT_START);
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

    public void registerUserToEvent(@Positive Long eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        Location location = locationService.findById(eventEntity.getLocation().getId());

        int capacity = location.getCapacity();

        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new EventAlreadyStartedException("Регистрация на мероприятие уже закрыто.");
        }

        if (userEventRegistrationEntityRepository.existsByUserIdAndEventId(userEntity.getId(), eventId)) {
            throw new AlreadyRegisteredException("Пользователь уже зарегистрирован на это мероприятие.");
        }

        int placeToBuy = eventEntity.getOccupiedPlaces() + 1;

        if (placeToBuy > eventEntity.getMaxPlaces() || placeToBuy > capacity) {
            throw new NotEnoughPlaceException("Недостаточно мест на мероприятии для бронирования");
        }

        eventEntity.setOccupiedPlaces(placeToBuy);

        createUserEventRegistration(userEntity, eventEntity);
        repository.save(eventEntity);
    }

    public void deleteEventRegistration(@Positive Long eventId) {
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

    public Event getEventById(@Positive Long eventId) {
        EventEntity event = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        return mapper.toModel(event);
    }

    public void cancelEventById(@Positive Long eventId) {
        validateAndGetEventEntity(eventId).setStatus(EventStatus.CANCELLED);
    }

    public Event updateEvent(Long eventId, Event model) {

        if (model.getDate().isBefore(LocalDateTime.now(clock))) {
            throw new EventTimeInPastException("Новое время мероприятия указано в прошлом!");
        }

        model.setId(eventId);
        isLocationPlanned(model, locationService.findById(model.getLocationId()));

        EventEntity oldEntity = validateAndGetEventEntity(eventId);
        Event oldEvent = mapper.toModel(oldEntity);
        EventEntity newEntity = createNewEntity(model, oldEntity);

        EventEntity saved = repository.save(newEntity);
        saveKafkaChangeLogToEventNotificator(saved, oldEvent, newEntity, oldEntity);

        return mapper.toModel(saved);
    }

    public List<Event> searchEventByFilter(EventSearchRequestDto eventSearchRequestDto) {
        List<EventEntity> entityList = repository.parseAndFindByFilter(eventSearchRequestDto);
        return mapper.toModel(entityList);
    }

    private void saveKafkaChangeLogToEventNotificator(EventEntity saved, Event oldEvent, EventEntity newEntity, EventEntity oldEntity) {
        eventKafkaProducer.sendEventChanges(
                new EventChangeKafkaMessage()
                        .setEventId(saved.getId())
                        .setChangedById(getSecurityUserId())
                        .setName(new FieldChange<>(oldEvent.getName(), newEntity.getName()))
                        .setMaxPlaces(new FieldChange<>(oldEvent.getMaxPlaces(), newEntity.getMaxPlaces()))
                        .setDate(new FieldChange<>(oldEvent.getDate(), newEntity.getDate()))
                        .setCost(new FieldChange<>(oldEvent.getCost(), newEntity.getCost()))
                        .setDuration(new FieldChange<>(oldEvent.getDuration(), newEntity.getDuration()))
                        .setLocationId(new FieldChange<>(oldEvent.getLocationId(), newEntity.getLocation().getId()))
                        .setStatus(new FieldChange<>(oldEvent.getStatus(), newEntity.getStatus()))
                        .setUsers(userEventRegistrationEntityRepository.findUserIdsByEventId(oldEntity.getId()))
        );
    }

    private EventEntity createNewEntity(Event model, EventEntity oldEntity) {
        LocationEntity location = locationService.getLocationFromDb(model.getLocationId());
        UserEntity userEntity = userService.getUserByIdForUser(getSecurityUserId());

        EventEntity newEntity = mapper.toEntity(model);
        newEntity.setId(oldEntity.getId());
        newEntity.setOwner(userEntity);
        newEntity.setLocation(location);
        newEntity.setStatus(EventStatus.WAIT_START);
        newEntity.setOccupiedPlaces(oldEntity.getOccupiedPlaces());
        validateNewEventFields(oldEntity, newEntity);

        return newEntity;
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

    private EventEntity validateAndGetEventEntity(Long eventId) {
        EventEntity eventEntity = repository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (!Objects.equals(getSecurityUserId(), eventEntity.getOwner().getId())) {
            throw new AuthorizationDeniedException("Вы не являетесь создателем мероприятия.");
        }

        return eventEntity;
    }

    private void createUserEventRegistration(UserEntity userEntity, EventEntity eventEntity) {
        UserEventRegistrationEntity registration = new UserEventRegistrationEntity();
        registration.setUser(userEntity);
        registration.setEvent(eventEntity);
        userEventRegistrationEntityRepository.save(registration);
    }

    private void isLocationPlanned(Event eventToCreate, Location location) {
        LocalDateTime startTime = eventToCreate.getDate();
        LocalDateTime requestEnd = startTime.plusMinutes(eventToCreate.getDuration());

        if (repository.isLocationPlanned(location.getId(),
                List.of(EventStatus.WAIT_START.toString(), EventStatus.STARTED.toString()),
                startTime,
                requestEnd,
                eventToCreate.getId()
        )) {
            throw new LocationIsPlannedException("Локация уже занята другим мероприятием");
        }
    }
}
