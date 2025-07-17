package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    boolean existsEventEntitiesByLocation(LocationEntity location);

    List<EventEntity> findEventByOwnerId(Long id);

    @Query("select e from EventEntity e where e.id = :eventId")
    @Lock(PESSIMISTIC_WRITE)
    Optional<EventEntity> findEventById(@Param("eventId") Integer id);

    boolean existsById(@Positive Integer eventId);
}
