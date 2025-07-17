package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.UserEventRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventRegistrationEntityRepository extends JpaRepository<UserEventRegistrationEntity, Integer> {
    boolean existsByUserIdAndEventId(Long userId, Integer eventId);
}
