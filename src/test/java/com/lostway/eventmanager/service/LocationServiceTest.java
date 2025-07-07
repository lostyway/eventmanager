package com.lostway.eventmanager.service;

import com.lostway.eventmanager.IntegrationTestBase;
import com.lostway.eventmanager.exception.LocationAlreadyExists;
import com.lostway.eventmanager.exception.LocationCapacityReductionException;
import com.lostway.eventmanager.exception.LocationNotFoundException;
import com.lostway.eventmanager.service.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class LocationServiceTest extends IntegrationTestBase {

    @Autowired
    LocationService service;

    private Location location;
    private Location saveLocationSecond;
    private Location newSecondLocation;

    @BeforeEach
    void setUp() {
        location = Location.builder()
                .name("LocationTestName")
                .address("AddressTest")
                .capacity(20)
                .description("DescriptionTest")
                .build();

        saveLocationSecond = Location.builder()
                .name("LocationTestName")
                .address("AddressTest")
                .capacity(20)
                .description("DescriptionTest")
                .build();

        newSecondLocation = Location.builder()
                .name("SecondName")
                .address("AddressSecond")
                .capacity(25)
                .description("DescriptionTest")
                .build();
    }

    @Test
    void whenAddNewLocationThenReturnNewLocation() {
        var savedLocation = service.createLocation(location);
        location.setId(savedLocation.getId());

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation).isEqualTo(location);
    }

    @Test
    void whenFindByIdIsSuccessful() {
        var savedLocation = service.createLocation(location);

        var findedLocation = service.findById(savedLocation.getId());

        assertThat(findedLocation).isNotNull();
        assertThat(findedLocation).isEqualTo(savedLocation);
    }

    @Test
    void whenFindAllIsSuccessful() {
        service.createLocation(location);
        service.createLocation(newSecondLocation);

        var list = service.getAll();

        assertThat(list).hasSize(2);
    }

    @Test
    void whenAddNewLocationFailedBecauseLocationAlreadyExists() {
        var savedLocation = service.createLocation(location);
        location.setId(savedLocation.getId());

        LocationAlreadyExists thrown = assertThrows(LocationAlreadyExists.class,
                () -> service.createLocation(saveLocationSecond));
        var savedLocationFirst = service.findById(savedLocation.getId());


        assertThat(thrown).hasMessageContaining("Такая локация уже существует!");
        assertThat(savedLocationFirst).isNotNull();
        assertThat(savedLocationFirst).isEqualTo(location);
    }

    @Test
    void whenSavedIsFailedBecauseOfIncorrectLocation() {
        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void whenUpdateIsSuccessful() {
        var savedLocation = service.createLocation(location);
        location.setId(savedLocation.getId());

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation).isEqualTo(location);

        var updatedLocation = service.updateLocation(savedLocation.getId(), newSecondLocation);
        newSecondLocation.setId(updatedLocation.getId());

        assertThat(updatedLocation).isNotNull();
        assertThat(updatedLocation).isEqualTo(newSecondLocation);
        assertThat(updatedLocation.getId()).isEqualTo(savedLocation.getId());
        assertThat(updatedLocation).isNotEqualTo(savedLocation);
    }

    @Test
    void whenUpdateIsFailedByLowCapacity() {
        var savedLocation = service.createLocation(location);
        location.setId(savedLocation.getId());

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation).isEqualTo(location);

        newSecondLocation.setCapacity(1);
        Exception exception = assertThrows(LocationCapacityReductionException.class,
        () -> service.updateLocation(savedLocation.getId(), newSecondLocation));

        assertThat(exception).hasMessageContaining("Нельзя уменьшить вместительность локации");
    }

    @Test
    void whenUpdateIsFailedByNotExistingLocation() {
        service.createLocation(location);

        Exception exception = assertThrows(LocationNotFoundException.class,
                () -> service.updateLocation(999, newSecondLocation));

        assertThat(exception).hasMessageContaining("Локация для удаления");
    }

    @Test
    void whenUpdateIsSuccessfulThenGetSaveCountOfLocations() {
        var locationToReplace = service.createLocation(location);
        var toUpdate = service.createLocation(newSecondLocation);

        assertThat(service.getAll()).hasSize(2);

        service.updateLocation(locationToReplace.getId(), toUpdate);

        assertThat(service.getAll()).hasSize(2);
        assertThat(service.findById(locationToReplace.getId())).isNotNull();
        assertThat(service.findById(locationToReplace.getId()).getName()).isEqualTo(toUpdate.getName());
        assertThat(service.findById(toUpdate.getId())).isNotNull();
        assertThat(service.findById(toUpdate.getId()).getName()).isEqualTo(toUpdate.getName());
    }
}