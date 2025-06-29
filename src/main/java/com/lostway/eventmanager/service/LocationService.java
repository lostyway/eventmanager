package com.lostway.eventmanager.service;

import com.lostway.eventmanager.exception.LocationCapacityReductionException;
import com.lostway.eventmanager.exception.LocationIsPlannedException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.mapper.LocationMapper;
import com.lostway.eventmanager.repository.LocationRepository;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.service.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationMapper mapper;
    private final LocationRepository repository;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        var list = repository.findAll();
        return mapper.toModelList(list);
    }

    @Transactional
    public Location createLocation(Location location) {
        LocationEntity entity = repository.save(mapper.toEntity(location));
        return mapper.toModel(entity);
    }

    /**
     * Проверяется локация, если она уже занята ивентом -> отказ.
     * Если свободна - возвращается
     *
     * @return Локация
     * @throws LocationIsPlannedException локация уже занята мероприятием
     * @throws LocationNotFoundException  локация не была найдена
     */
    @Transactional
    public Location removeById(Integer locationId) {
        LocationEntity locationEntity = repository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Локация с ID: '%s' не была найдена".formatted(locationId)));

        if (isLocationPlanned(locationEntity)) {
            throw new LocationIsPlannedException("Локация уже занята мероприятием");
        }

        Location location = mapper.toModel(locationEntity);
        repository.delete(locationEntity);

        return location;
    }

    private boolean isLocationPlanned(LocationEntity locationEntity) {
        return eventService.isLocationPlanned(locationEntity);
    }

    @Transactional
    public Location findById(Integer locationId) {
        LocationEntity locationEntity = repository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Локация с ID: '%s' не была найдена".formatted(locationId)));

        return mapper.toModel(locationEntity);
    }

    @Transactional
    public Location updateLocation(Integer locationId, Location updateLocation) {
        LocationEntity existing = repository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Локация для удаления: '%s' не была найдена.".formatted(locationId)));

        if (existing.getCapacity() > updateLocation.getCapacity()) {
            throw new LocationCapacityReductionException("Нельзя уменьшить вместительность локации");
        }

        LocationEntity toSave = mapper.toEntity(updateLocation);
        toSave.setId(existing.getId());

        return mapper.toModel(repository.save(toSave));
    }
}
