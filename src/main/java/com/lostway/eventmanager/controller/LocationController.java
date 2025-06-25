package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.LocationDto;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.service.LocationService;
import com.lostway.eventmanager.service.model.Location;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService service;
    private final LocationMapper mapper;

    @GetMapping
    public ResponseEntity<?> getAll() {
        var list = service.getAll();
        return ResponseEntity.ok(mapper.toDtoList(list));
    }

    @PostMapping
    public ResponseEntity<?> createLocation(@RequestBody @Valid LocationDto dto) {
        Location location = service.createLocation(mapper.toModel(dto));
        LocationDto locationDto = mapper.toDto(location);
        return ResponseEntity.ok(locationDto);
    }

    /**
     * Нельзя удалять локации если на них уже есть мероприятие.
     * @return Локация которую удалили
     */
    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Integer locationId) {
        try {
            Location location = service.removeById(locationId);
            return ResponseEntity.ok(mapper.toDto(location));
        } catch (LocationIsPlannedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
