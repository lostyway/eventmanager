package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LocationRepositoryTest extends IntegrationTestBase {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testLocationRepository() {
        var locationEntity = LocationEntity.builder()
                .name("locationTest")
                .address("AddressTest")
                .capacity(20)
                .description("DescriptionTest")
                .build();

        var savedEntity = locationRepository.save(locationEntity);
        var locationFindByIdOpt = locationRepository.findById(savedEntity.getId());

        assertThat(locationFindByIdOpt.isPresent()).isTrue();
        assertThat(locationFindByIdOpt.get().getName()).isEqualTo(locationEntity.getName());
        assertThat(locationFindByIdOpt.get().getCapacity()).isEqualTo(locationEntity.getCapacity());
    }

}