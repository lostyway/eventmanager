package com.lostway.eventmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lostway.eventmanager.config.TestSecurityConfig;
import com.lostway.eventmanager.controller.dto.EventCreateRequestDto;
import com.lostway.eventmanager.controller.dto.EventDto;
import com.lostway.eventmanager.controller.dto.EventUpdateRequestDto;
import com.lostway.eventmanager.enums.EventStatus;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}