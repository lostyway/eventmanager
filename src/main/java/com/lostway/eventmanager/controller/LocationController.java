package com.lostway.eventmanager.controller;

import com.lostway.eventmanager.controller.dto.LocationDto;
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
        return ResponseEntity.status(201).body(locationDto);
    }

    /**
     * Нельзя удалять локации если на них уже есть мероприятие.
     *
     * @return Локация которую удалили
     */
    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Integer locationId) {
        Location location = service.removeById(locationId);
        return ResponseEntity.ok(mapper.toDto(location));
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<?> getLocationById(@PathVariable Integer locationId) {
        Location location = service.findById(locationId);
        return ResponseEntity.ok(mapper.toDto(location));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer locationId,
                                            @RequestBody @Valid LocationDto dto) {
        Location updateLocation = mapper.toModel(dto);
        Location updated = service.updateLocation(locationId, updateLocation);
        return ResponseEntity.ok(mapper.toDto(updated));
    }
}
