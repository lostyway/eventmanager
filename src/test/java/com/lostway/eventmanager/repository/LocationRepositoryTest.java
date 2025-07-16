package com.lostway.eventmanager.repository;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.repository.entity.LocationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LocationRepositoryTest extends IntegrationTestBase {

    @Autowired
    private LocationRepository locationRepository;

    private LocationEntity locationTest;
    private LocationEntity locationTestSecond;

    @BeforeEach
    void setUp() {
        locationTest = LocationEntity.builder()
                .name("locationTest")
                .address("AddressTest")
                .capacity(20)
                .description("DescriptionTest")
                .build();

        locationTestSecond = LocationEntity.builder()
                .name("locationTest")
                .address("AddressTest")
                .capacity(20)
                .description("DescriptionTest")
                .build();
    }

    @Test
    void testLocationRepository() {
        var savedEntity = locationRepository.save(locationTest);
        var locationFindByIdOpt = locationRepository.findById(savedEntity.getId());

        assertThat(locationFindByIdOpt.isPresent()).isTrue();
        assertThat(locationFindByIdOpt.get().getName()).isEqualTo(locationTest.getName());
        assertThat(locationFindByIdOpt.get().getCapacity()).isEqualTo(locationTest.getCapacity());
    }

    @Test
    void whenDeleteLocationThenSuccess() {
        var savedEntity = locationRepository.save(locationTest);
        locationRepository.delete(savedEntity);
        var findDeleteOpt = locationRepository.findById(savedEntity.getId());

        assertThat(findDeleteOpt.isPresent()).isFalse();
    }

    @Test
    void whenDeleteLocationThenFailedThenFindEntity() {
        var savedEntity = locationRepository.save(locationTest);
        var savedEntityTwo = locationRepository.save(locationTestSecond);
        locationRepository.delete(savedEntityTwo);

        var findDeleteOpt = locationRepository.findById(savedEntity.getId());
        var findDeleteOptTwo = locationRepository.findById(savedEntityTwo.getId());

        assertThat(findDeleteOpt.isPresent()).isTrue();
        assertThat(findDeleteOptTwo.isPresent()).isFalse();
    }

    @Test
    void whenFindAllLocations() {
        var listEntity = locationRepository.findAll();
        assertThat(listEntity).size().isEqualTo(0);

        locationRepository.save(locationTest);
        locationRepository.save(locationTestSecond);

        var listEntityFinalWithEntity = locationRepository.findAll();

        assertThat(listEntityFinalWithEntity).size().isEqualTo(2);
    }
}