package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;
import com.covaliroman.parkify.support.PostgresTestContainerConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PostgresTestContainerConfig.class)
class ParkingFacilityRepositoryTest {

    @Autowired
    private ParkingFacilityRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistAndLoadParkingFacility() {
        var facility = new ParkingFacility(
                "Central Parking",
                "31 August 1989 Street, Chisinau",
                new BigDecimal("47.010500"),
                new BigDecimal("28.863800")
        );

        var saved = repository.saveAndFlush(facility);
        var facilityId = saved.getId();
        entityManager.clear();

        var reloaded = repository.findById(facilityId);

        assertThat(reloaded).isPresent().get().satisfies(actual -> {
            assertThat(actual.getId()).isEqualTo(facilityId);
            assertThat(actual.getName()).isEqualTo("Central Parking");
            assertThat(actual.getAddress()).isEqualTo("31 August 1989 Street, Chisinau");
            assertThat(actual.getLatitude()).isEqualByComparingTo("47.010500");
            assertThat(actual.getLongitude()).isEqualByComparingTo("28.863800");
            assertThat(actual.getStatus()).isEqualTo(ParkingFacilityStatus.ACTIVE);
            assertThat(actual.getCreatedAt()).isNotNull();
            assertThat(actual.getUpdatedAt()).isNotNull();
        });
    }

    @Test
    void shouldPersistFacilityWithoutCoordinates() {
        var facility = new ParkingFacility(
                "Underground Parking",
                "Stefan cel Mare Boulevard, Chisinau",
                null,
                null
        );

        var saved = repository.saveAndFlush(facility);
        entityManager.clear();

        assertThat(repository.findById(saved.getId()))
                .isPresent()
                .get()
                .satisfies(actual -> {
                    assertThat(actual.getLatitude()).isNull();
                    assertThat(actual.getLongitude()).isNull();
                });
    }
}
