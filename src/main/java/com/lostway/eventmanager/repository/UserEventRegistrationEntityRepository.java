package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.UserEventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEventRegistrationEntityRepository extends JpaRepository<UserEventRegistrationEntity, Integer> {
    boolean existsByUserIdAndEventId(Long userId, Integer eventId);

    Optional<UserEventRegistrationEntity> findByUserIdAndEventId(Long userId, Integer eventId);
}
