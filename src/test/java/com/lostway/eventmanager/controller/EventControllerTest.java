package com.lostway.eventmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostway.eventmanager.config.TestSecurityConfig;
import com.lostway.eventmanager.controller.dto.EventCreateRequestDto;
import com.lostway.eventmanager.controller.dto.EventDto;
import com.lostway.eventmanager.controller.dto.EventSearchRequestDto;
import com.lostway.eventmanager.controller.dto.EventUpdateRequestDto;
import com.lostway.eventmanager.enums.EventStatus;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.mapper.EventMapper;
import com.lostway.eventmanager.mapper.EventMapperImpl;
import com.lostway.eventmanager.security.JWTAuthFilter;
import com.lostway.eventmanager.service.EventService;
import com.lostway.eventmanager.service.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthFilter.class)
})
@Import({TestSecurityConfig.class, EventMapperImpl.class})
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private EventMapper mapper;

    @Autowired
    private EventController eventController;

    private EventUpdateRequestDto eventUpdateRequestDto;
    private EventCreateRequestDto eventCreateRequestDto;
    private Event event;

    @Autowired
    private ObjectMapper jacksonObjectMapper;
    private EventSearchRequestDto eventSearchRequestDto;

    private EventDto createTestEventDto() {
        return new EventDto(
                1,
                "Test Event",
                123L,
                10,
                5,
                LocalDateTime.now().plusDays(1),
                1000,
                60,
                1,
                EventStatus.WAIT_START
        );
    }

    @BeforeEach
    void setUp() {
        eventUpdateRequestDto = new EventUpdateRequestDto(
                "Updated Event",
                15,
                LocalDateTime.now().plusDays(2),
                1500,
                90,
                2
        );

        eventCreateRequestDto = new EventCreateRequestDto(
                "New Event",
                10,
                LocalDateTime.now().plusDays(1),
                1000,
                60,
                1
        );

        event = Event.builder()
                .id(1)
                .name("Test Event")
                .cost(100)
                .date(LocalDateTime.of(25, 2, 3, 12, 0))
                .duration(30)
                .locationId(1)
                .maxPlaces(250)
                .occupiedPlaces(0)
                .ownerId(1L)
                .status(EventStatus.WAIT_START)
                .build();

        eventSearchRequestDto = new EventSearchRequestDto("Test Event",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }


    @Test
    void whenCreateNewEventIsSuccessful() throws Exception {
        when(eventService.createNewEvent(any())).thenReturn(event);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(eventCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(event.getName()));
    }

    @Test
    void whenGetMyEventsIsSuccessful() throws Exception {
        when(eventService.getUsersEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(event.getName()));
    }

    @Test
    void whenRegisterNewEventIsSuccessful() throws Exception {
        mockMvc.perform(post("/events/registrations/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Успешная регистрация на мероприятие"));
    }

    @Test
    void whenCancelEventIsSuccessful() throws Exception {
        mockMvc.perform(delete("/events/registrations/cancel/{eventId}", event.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenGetUserRegistrationsIsSuccessful() throws Exception {
        when(eventService.getUserRegistrationsOnEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/events/registrations/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(event.getName()));
    }

    @Test
    void whenGetEventByIdIsSuccessful() throws Exception {
        when(eventService.getEventById(event.getId())).thenReturn(event);

        mockMvc.perform(get("/events/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(event.getName()));
    }

    @Test
    void whenDeleteEventByIdIsSuccessful() throws Exception {
        mockMvc.perform(delete("/events/{eventId}", event.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenUpdateEventIsSuccessful() throws Exception {
        Event newEvent = new Event(1, "newName", 10, LocalDateTime.now(),
                100, 5, 30, 1, 1L, EventStatus.WAIT_START);
        when(eventService.updateEvent(eq(event.getId()), any(Event.class))).thenReturn(newEvent);
        mockMvc.perform(put("/events/{eventId}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(eventUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newEvent.getName()))
                .andExpect(jsonPath("$.cost").value(newEvent.getCost()))
                .andExpect(jsonPath("$.status").value(newEvent.getStatus().toString()));
    }

    @Test
    void whenSearchEventIsSuccessful() throws Exception {
        when(eventService.searchEventByFilter(any())).thenReturn(List.of(event));
        mockMvc.perform(post("/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(eventSearchRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(event.getName()))
                .andExpect(jsonPath("$[0].status").value(event.getStatus().toString()));
    }

    @Test
    void whenCreateEventIsFailedByNameBadParams() throws Exception {
        String invalidJson = """
                {
                   "name": "",
                   "maxPlaces": "10",
                   "date": "2099-01-01T00:00:00",
                   "cost": "100",
                   "duration": "30",
                   "locationId": "1"
                }
                """;
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Данные сущности введены некорректно"))
                .andExpect(jsonPath("$.detailedMessage").value("Поле 'name' : must not be blank"))
                .andExpect(jsonPath("$.dateTime").exists());

    }

    @Test
    void whenCreateEventIsFailedByMaxPlacesBadParams() throws Exception {
        String invalidJson = """
                {
                   "name": "name",
                   "maxPlaces": "-1",
                   "date": "2099-01-01T00:00:00",
                   "cost": "100",
                   "duration": "30",
                   "locationId": "1"
                }
                """;
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Данные сущности введены некорректно"))
                .andExpect(jsonPath("$.detailedMessage").value("Поле 'maxPlaces' : must be greater than 0"))
                .andExpect(jsonPath("$.dateTime").exists());

    }

    @Test
    void whenCreateEventIsFailedByMoreThenOneBadParams() throws Exception {
        String invalidJson = """
                {
                   "name": "",
                   "maxPlaces": "-1",
                   "date": "2099-01-01T00:00:00",
                   "cost": "100",
                   "duration": "30",
                   "locationId": "1"
                }
                """;
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Данные сущности введены некорректно"))
                .andExpect(jsonPath("$.detailedMessage").value(containsString("Поле 'maxPlaces' : must be greater than 0")))
                .andExpect(jsonPath("$.detailedMessage").value(containsString("Поле 'name' : must not be blank")))
                .andExpect(jsonPath("$.dateTime").exists());

    }

    @Test
    void whenCreateNewEventFailedByPlannedLocation() throws Exception {
        when(eventService.createNewEvent(any())).thenThrow(new LocationIsPlannedException("Локация уже занята другим мероприятием"));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jacksonObjectMapper.writeValueAsString(eventCreateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Локация уже занята мероприятием"))
                .andExpect(jsonPath("$.detailedMessage").value("Локация уже занята другим мероприятием"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void whenCallInvalidUrl() throws Exception {
        mockMvc.perform(post("/random"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Данный адрес не был найден"))
                .andExpect(jsonPath("$.detailedMessage").value("No static resource random."))
                .andExpect(jsonPath("$.dateTime").exists());
    }
}