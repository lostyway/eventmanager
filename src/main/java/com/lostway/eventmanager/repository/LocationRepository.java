package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
    boolean existsByNameAndAddressAndCapacity(String name, String address, Integer capacity);
    Page<LocationEntity> findAll(Pageable pageable);
}
