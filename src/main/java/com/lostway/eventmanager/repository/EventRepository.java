package com.lostway.eventmanager.repository;

import com.lostway.eventdtos.EventStatus;
import com.lostway.eventmanager.controller.dto.EventSearchRequestDto;
import com.lostway.eventmanager.repository.entity.EventEntity;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_READ;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query(value = """
                SELECT EXISTS(
                    SELECT 1
                    FROM events e
                    WHERE e.location_id = :locationId
                      AND e.status IN (:statuses)
                      AND (
                          e.date < :requestedEnd
                          AND e.date + (e.duration * interval '1 minute') > :requestedStart
                      )
                    AND (:eventId IS NULL OR e.id != :eventId)
                )
            """, nativeQuery = true)
    boolean isLocationPlanned(
            @Param("locationId") Long locationId,
            @Param("statuses") List<String> statuses,
            @Param("requestedStart") LocalDateTime requestedStart,
            @Param("requestedEnd") LocalDateTime requestedEnd,
            @Param("eventId") Long eventId
    );

    boolean existsEventEntitiesByLocationAndStatusIn(LocationEntity location, List<EventStatus> statues);

    List<EventEntity> findEventByOwnerId(Long id);

    @Query("select e from EventEntity e where e.id = :eventId")
    @Lock(PESSIMISTIC_WRITE)
    Optional<EventEntity> findEventById(@Param("eventId") Long id);

    List<EventEntity> findByStatusIn(List<EventStatus> statues);

    @Query("""
               select e from EventEntity e
               where (:name is null or lower(e.name) like :name)
                and (:placesMin is null or e.maxPlaces >= :placesMin)
                and (:placesMax is null or e.maxPlaces <= :placesMax)
                and (cast(:dateStartAfter as date) is null or e.date >= :dateStartAfter)
                and (cast(:dateStartBefore as date) is null or e.date <= :dateStartBefore)
                and (:costMin is null or e.cost >= :costMin)
                and (:costMax is null or e.cost <= :costMax)
                and (:durationMin is null or e.duration >= :durationMin)
                and (:durationMax is null or e.duration <= :durationMax)
                and (:locationId is null or e.location.id = :locationId)
                and (:eventStatus is null or e.status = :eventStatus)
            """)
    @Lock(PESSIMISTIC_READ)
    List<EventEntity> findByFilter(@Param("name") String name,
                                   @Param("placesMin") Integer placesMin,
                                   @Param("placesMax") Integer placesMax,
                                   @Param("dateStartAfter") LocalDateTime dateStartAfter,
                                   @Param("dateStartBefore") LocalDateTime dateStartBefore,
                                   @Param("costMin") Integer costMin,
                                   @Param("costMax") Integer costMax,
                                   @Param("durationMin") Integer durationMin,
                                   @Param("durationMax") Integer durationMax,
                                   @Param("locationId") Long locationId,
                                   @Param("eventStatus") EventStatus eventStatus
    );

    default List<EventEntity> parseAndFindByFilter(EventSearchRequestDto eventSearchRequestDto) {
        String name = eventSearchRequestDto.name() == null
                ? null
                : "%" + eventSearchRequestDto.name().toLowerCase() + "%";
        Integer placesMin = eventSearchRequestDto.placesMin();
        Integer placesMax = eventSearchRequestDto.placesMax();
        LocalDateTime dateStartAfter = eventSearchRequestDto.dateStartAfter();
        LocalDateTime dateStartBefore = eventSearchRequestDto.dateStartBefore();
        Integer costMin = eventSearchRequestDto.costMin();
        Integer costMax = eventSearchRequestDto.costMax();
        Integer durationMin = eventSearchRequestDto.durationMin();
        Integer durationMax = eventSearchRequestDto.durationMax();
        Long locationId = eventSearchRequestDto.locationId();
        EventStatus eventStatus = eventSearchRequestDto.eventStatus();

        return findByFilter(
                name,
                placesMin,
                placesMax,
                dateStartAfter,
                dateStartBefore,
                costMin,
                costMax,
                durationMin,
                durationMax,
                locationId,
                eventStatus
        );
    }
}
