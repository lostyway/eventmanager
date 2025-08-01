package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.UserEntity;
import com.lostway.eventmanager.repository.entity.UserEventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserEventRegistrationEntityRepository extends JpaRepository<UserEventRegistrationEntity, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<UserEventRegistrationEntity> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserEventRegistrationEntity> findByUser(UserEntity user);

    @Query("select u.user.id from UserEventRegistrationEntity u where u.event.id = :eventId")
    List<Long> findUserIdsByEventId(@Param("eventId") Long eventId);
}
