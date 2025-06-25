package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
}
