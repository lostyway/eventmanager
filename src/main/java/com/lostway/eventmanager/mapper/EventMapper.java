package com.lostway.eventmanager.mapper;

import com.lostway.eventmanager.controller.dto.EventCreateRequestDto;
import com.lostway.eventmanager.controller.dto.EventDto;
import com.lostway.eventmanager.controller.dto.EventUpdateRequestDto;
import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.service.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toModel(EventCreateRequestDto eventCreateRequestDto);

    List<EventDto> toDto(List<Event> events);

    List<Event> toModel(List<EventEntity> events);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "locationId", source = "location.id")
    Event toModel(EventEntity eventEntity);

    EventDto toDto(Event newEventCreated);

    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "mapOwner")
    @Mapping(target = "location", source = "locationId", qualifiedByName = "mapLocation")
    EventEntity toEntity(Event event);

    @Named("mapOwner")
    default UserEntity mapOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(ownerId);
        return user;
    }

    @Named("mapLocation")
    default LocationEntity mapLocation(Integer locationId) {
        if (locationId == null) {
            return null;
        }
        LocationEntity location = new LocationEntity();
        location.setId(locationId);
        return location;
    }

    Event toModel(EventUpdateRequestDto eventUpdateRequestDto);
}
