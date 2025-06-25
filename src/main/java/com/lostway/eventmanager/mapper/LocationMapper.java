package com.lostway.eventmanager.mapper;

import com.lostway.eventmanager.controller.dto.LocationDto;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.service.model.Location;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toDto(Location model);

    Location toModel(LocationDto dto);

    Location toModel(LocationEntity entity);

    LocationEntity toEntity(Location model);

    List<LocationDto> toDtoList(List<Location> models);

    List<Location> toModelList(List<LocationEntity> entities);
}
