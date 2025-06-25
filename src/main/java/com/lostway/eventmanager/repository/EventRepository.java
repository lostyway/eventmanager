package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    boolean existsEventEntitiesByLocation(LocationEntity location);
}
