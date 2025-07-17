package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    boolean existsEventEntitiesByLocation(LocationEntity location);

    List<EventEntity> findEventByOwnerId(long id);
}
