package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.controller.dto.LocationDto;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.service.LocationService;
import com.lostway.eventmanager.service.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class LocationControllerTest extends IntegrationTestBase {

    @Autowired
    private LocationController locationController;

    @MockitoBean
    private LocationMapper mapper;

    @MockitoBean
    private LocationService locationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private LocationDto locationDto;

    private Page<Location> locationPage;

    private Location location;

    private LocationDto locationToUpdateDto;

    private Location locationToUpdate;

    private LocationDto updatedLocationDto;

    @BeforeEach
    void init() {
        locationDto = new LocationDto(1, "test", "address", 10, "desctest");
        location = new Location(1, "test", "address", 10, "desctest");
        locationPage = new PageImpl<>(
                List.of(new Location(1, "testloc", "addressTest", 20, "desc"),
                        new Location(1, "testloc", "addressTest", 20, "desc")),
                PageRequest.of(0, 10),
                2
        );

        locationToUpdateDto = new LocationDto(
                0,
                "testNameNew",
                "addressNew",
                locationDto.getCapacity() + 10,
                "newDesc"
        );

        locationToUpdate = new Location(
                0,
                "testNameNew",
                "addressNew",
                locationDto.getCapacity() + 10,
                "newDesc"
        );

        updatedLocationDto = new LocationDto(
                locationDto.getId(),
                "testNameNew",
                "addressNew",
                locationDto.getCapacity() + 10,
                "newDesc"
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoLocations() throws Exception {
        Page<Location> emptyPage = Page.empty();
        when(locationService.getAll(any())).thenReturn(emptyPage);
        when(mapper.toDto(any())).thenReturn(locationDto);

        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void whenHaveOneLocationThenGetOneLocation() throws Exception {
        when(locationService.getAll(any())).thenReturn(locationPage);
        when(mapper.toDto(any())).thenReturn(locationDto);

        mockMvc.perform(get("/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void whenCreatingLocationIsSuccessful() throws Exception {
        when(locationService.createLocation(any())).thenReturn(location);
        when(mapper.toDto(any())).thenReturn(locationDto);

        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(locationDto.getId()))
                .andExpect(jsonPath("$.name").value(locationDto.getName()));
    }

    @Test
    void whenCreationLocationIsFailedByBadDto() throws Exception {
        LocationDto wrongDto = new LocationDto(1, "test", "address", 3, "desctest");
        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Данные сущности введены некорректно"))
                .andExpect(jsonPath("$.detailedMessage").value("Поле 'capacity' : Заполненность должна быть больше 5"));
    }

    @Test
    void whenDeleteLocationIsSuccessful() throws Exception {
        Integer id = locationDto.getId();
        when(locationService.removeById(id)).thenReturn(location);
        when(mapper.toDto(any())).thenReturn(locationDto);

        mockMvc.perform(delete("/locations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(location.getName()));
    }

    @Test
    void whenDeleteLocationIsWrongByWrongIdThenGetException() throws Exception {
        Integer id = -1;
        String errorMessage = "Локация с ID: '%s' не была найдена".formatted(id);
        when(locationService.removeById(id)).thenThrow(new LocationNotFoundException(errorMessage));
        when(mapper.toDto(any())).thenReturn(null);

        mockMvc.perform(delete("/locations/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Сущность не найдена"))
                .andExpect(jsonPath("$.detailedMessage").value(errorMessage));
    }

    @Test
    void whenDeleteLocationIsWrongByLocationIsPlanned() throws Exception {
        Integer id = -1;
        String errorMessage = "Локация уже занята мероприятием";
        when(locationService.removeById(id)).thenThrow(new LocationIsPlannedException(errorMessage));
        when(mapper.toDto(any())).thenReturn(null);

        mockMvc.perform(delete("/locations/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Локация уже занята мероприятием"))
                .andExpect(jsonPath("$.detailedMessage").value(errorMessage));
    }

    @Test
    void whenGetLocationByIdIsSuccessful() throws Exception {
        Integer id = locationDto.getId();
        when(locationService.findById(id)).thenReturn(location);
        when(mapper.toDto(any())).thenReturn(locationDto);

        mockMvc.perform(get("/locations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(locationDto.getName()));
    }

    @Test
    void whenGetLocationByIdIsFailedByBadId() throws Exception {
        Integer id = -1;
        String errorMessage = "Локация с ID: '%s' не была найдена".formatted(id);
        when(locationService.findById(id)).thenThrow(new LocationNotFoundException(errorMessage));
        when(mapper.toDto(any())).thenReturn(null);

        mockMvc.perform(get("/locations/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Сущность не найдена"))
                .andExpect(jsonPath("$.detailedMessage").value(errorMessage));
    }

    @Test
    void whenUpdateLocationIsSuccessful() throws Exception {
        when(locationService.updateLocation(locationDto.getId(), locationToUpdate)).thenReturn(locationToUpdate);
        when(mapper.toDto(any())).thenReturn(updatedLocationDto);

        mockMvc.perform(put("/locations/" + locationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationToUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(locationDto.getId()))
                .andExpect(jsonPath("$.name").value(locationToUpdateDto.getName()));
    }
}