package com.lostway.eventmanager.service;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.enums.Role;
import com.lostway.eventmanager.exception.*;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.repository.EventRepository;
import com.lostway.eventmanager.repository.LocationRepository;
import com.lostway.eventmanager.repository.UserRepository;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.service.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.lostway.eventmanager.enums.EventStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventServiceTest extends IntegrationTestBase {
    private UserEntity user;
    private UserEntity admin;
    private LocationEntity locationEntity;
    private LocationEntity locationEntity2;
    private Event event;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new UserEntity(null, "user", "pass", 22, Role.USER));
        admin = userRepository.save(new UserEntity(null, "adminLogin", "pass", 22, Role.ADMIN));
        locationEntity = locationRepository.save(new LocationEntity(null, "location", "address", 1000, "desc"));
        locationEntity2 = locationRepository.save(new LocationEntity(null, "location2", "address", 1000, "desc"));
        event = new Event(
                null,
                "event name",
                10,
                LocalDateTime.of(2025, 12, 11, 0, 0), 100.0, 0, 30,
                locationEntity.getId(),
                user.getId(),
                WAIT_START);
    }

    @Test
    void whenCreatingEventIsSuccessful() {
        Event created = eventService.createNewEvent(event);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(event.getName());
    }

    @Test
    void whenCreatingEventIsFailedByEvent() {
        event = eventService.createNewEvent(event);

        Exception exception = assertThrows(LocationIsPlannedException.class, () -> eventService.createNewEvent(event));
        assertThat(exception).hasMessageContaining("Локация уже занята другим мероприятием");
    }

    @Test
    void whenCreatingEventIsFailedByMaxPlaces() {
        event.setMaxPlaces(9999);

        Exception exception = assertThrows(CapacityNotEnoughException.class, () -> eventService.createNewEvent(event));
        assertThat(exception).hasMessageContaining("Мест на локации меньше чем предполагается на мероприятии");
    }

    @Test
    void whenCreatingEventIsFailedByBadParamsInEvent() {
        event.setLocationId(-1L);

        Exception exception = assertThrows(LocationNotFoundException.class, () -> eventService.createNewEvent(event));
        assertThat(exception).hasMessageContaining("не была найдена");
    }

    @Test
    void whenGetUsersEventsIsSuccessful() {
        eventService.createNewEvent(event);
        event.setId(null);
        event.setName("test2");
        event.setLocationId(locationEntity2.getId());
        eventService.createNewEvent(event);

        var events = eventService.getUsersEvents();
        assertThat(events).hasSize(2);
    }

    @Test
    void whenGetUsersEventsIsSuccessfulWithEmptyList() {
        var events = eventService.getUsersEvents();
        assertThat(events).isEmpty();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class WhenNeedEventCreated {
        private Event temp;

        @BeforeEach
        void setUp() {
            temp = eventService.createNewEvent(event);
        }

        @Test
        void whenRegisterNewEventIsSuccessful() {
            eventService.registerNewEvent(temp.getId());
            int countOfMembers = eventService.getUsersEvents().getFirst().getOccupiedPlaces();
            assertThat(countOfMembers).isEqualTo(1);
        }

        @Test
        void whenRepeatRegisterThenFailed() {
            eventService.registerNewEvent(temp.getId());
            int countOfMembers = eventService.getUsersEvents().getFirst().getOccupiedPlaces();
            assertThat(countOfMembers).isEqualTo(1);
            Exception exception = assertThrows(AlreadyRegisteredException.class, () -> eventService.registerNewEvent(temp.getId()));
            assertThat(exception).hasMessageContaining("Пользователь уже зарегистрирован на это мероприятие.");
        }

        @Test
        void whenRegisterNewEventIsFailedByBadEventId() {
            Exception exception = assertThrows(EventNotFoundException.class, () -> eventService.registerNewEvent(-1L));
            assertThat(exception).hasMessageContaining("не было найдено");
        }

        @Test
        void whenDeleteEventRegistrationIsSuccessful() {
            eventService.registerNewEvent(temp.getId());
            eventService.deleteEventRegistration(temp.getId());
            int countOfMembers = eventService.getUsersEvents().getFirst().getOccupiedPlaces();
            assertThat(countOfMembers).isZero();
        }

        @Test
        void whenDeleteEventRegistrationIsSuccessfulCheckMembers() {
            eventService.registerNewEvent(temp.getId());
            int countOfMembers = eventService.getUsersEvents().getFirst().getOccupiedPlaces();
            assertThat(countOfMembers).isEqualTo(1);
            eventService.deleteEventRegistration(temp.getId());
            int countOfMembersAfterDelete = eventService.getUsersEvents().getFirst().getOccupiedPlaces();
            assertThat(countOfMembersAfterDelete).isZero();
        }

        @Test
        void whenGetUserRegistrationsOnEventsThenOne() {
            eventService.registerNewEvent(temp.getId());
            var events = eventService.getUserRegistrationsOnEvents();
            assertThat(events).hasSize(1);
        }

        @Test
        void whenGetUserRegistrationsOnEventsThenTwo() {
            Event newEvent = new Event(
                    null,
                    "event name2",
                    20,
                    LocalDateTime.of(2025, 12, 11, 0, 0), 100.0, 0, 30,
                    locationEntity2.getId(),
                    user.getId(),
                    WAIT_START);

            newEvent = eventService.createNewEvent(newEvent);
            eventService.registerNewEvent(temp.getId());
            var events = eventService.getUserRegistrationsOnEvents();
            assertThat(events).hasSize(1);
            eventService.registerNewEvent(newEvent.getId());
            var eventsTwo = eventService.getUserRegistrationsOnEvents();
            assertThat(eventsTwo).hasSize(2);
        }
    }

    @Test
    void whenRegisterNewEventIsFailedByBadStatus() {
        event = eventService.createNewEvent(event);
        event.setStatus(STARTED);
        eventRepository.save(eventMapper.toEntity(event));
        Exception exception = assertThrows(EventAlreadyStartedException.class, () -> eventService.registerNewEvent(event.getId()));
        assertThat(exception).hasMessageContaining("Регистрация на мероприятие уже закрыто.");
    }

    @Test
    void whenRegisterNewEventIsFailedByCapacity() {
        event.setOccupiedPlaces(1000);
        event = eventService.createNewEvent(event);
        Exception exception = assertThrows(NotEnoughPlaceException.class, () -> eventService.registerNewEvent(event.getId()));
        assertThat(exception).hasMessageContaining("Недостаточно мест на мероприятии для бронирования");
    }

    @Test
    void whenDeleteEventRegistrationIsBadForStatus1() {
        event = eventService.createNewEvent(event);
        event.setStatus(STARTED);
        eventRepository.save(eventMapper.toEntity(event));
        Exception exception = assertThrows(EventAlreadyStartedException.class, () -> eventService.deleteEventRegistration(event.getId()));
        assertThat(exception).hasMessageContaining("Регистрация на мероприятие уже закрыто. Отменить регистрацию не получится");
    }

    @Test
    void whenDeleteEventRegistrationIsBadForStatus2() {
        event = eventService.createNewEvent(event);
        event.setStatus(FINISHED);
        eventRepository.save(eventMapper.toEntity(event));
        Exception exception = assertThrows(EventAlreadyStartedException.class, () -> eventService.deleteEventRegistration(event.getId()));
        assertThat(exception).hasMessageContaining("Регистрация на мероприятие уже закрыто. Отменить регистрацию не получится");
    }

    @Test
    void whenDeleteEventRegistrationIsBadForStatus3() {
        event = eventService.createNewEvent(event);
        event.setStatus(CANCELLED);
        eventRepository.save(eventMapper.toEntity(event));
        Exception exception = assertThrows(EventAlreadyStartedException.class, () -> eventService.deleteEventRegistration(event.getId()));
        assertThat(exception).hasMessageContaining("Регистрация на мероприятие уже закрыто. Отменить регистрацию не получится");
    }
}