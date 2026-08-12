package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingLevel;
import com.covaliroman.parkify.support.PostgresTestContainerConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PostgresTestContainerConfig.class)
class ParkingLevelRepositoryTest {

    @Autowired
    private ParkingFacilityRepository facilityRepository;

    @Autowired
    private ParkingLevelRepository levelRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistAndLoadParkingLevel() {
        var facilityId = persistFacility("Central Parking").getId();
        var level = new ParkingLevel(facilityId, "B1", "Basement 1", 1);

        var saved = levelRepository.saveAndFlush(level);
        var levelId = saved.getId();
        entityManager.clear();

        assertThat(levelRepository.findById(levelId)).isPresent().get().satisfies(actual -> {
            assertThat(actual.getFacilityId()).isEqualTo(facilityId);
            assertThat(actual.getCode()).isEqualTo("B1");
            assertThat(actual.getDisplayName()).isEqualTo("Basement 1");
            assertThat(actual.getSortOrder()).isEqualTo(1);
            assertThat(actual.getCreatedAt()).isNotNull();
            assertThat(actual.getUpdatedAt()).isNotNull();
        });
    }

    @Test
    void shouldFindFacilityLevelsInSortOrderAndCheckCodeExistence() {
        var facilityId = persistFacility("Central Parking").getId();
        var otherFacilityId = persistFacility("Airport Parking").getId();
        levelRepository.saveAllAndFlush(java.util.List.of(
                new ParkingLevel(facilityId, "L2", "Level 2", 20),
                new ParkingLevel(otherFacilityId, "L1", "Other Level", 1),
                new ParkingLevel(facilityId, "L1", "Level 1", 10)
        ));
        entityManager.clear();

        var levels = levelRepository.findAllByFacilityIdOrderBySortOrderAsc(facilityId);

        assertThat(levels)
                .extracting(ParkingLevel::getCode)
                .containsExactly("L1", "L2");
        assertThat(levelRepository.existsByFacilityIdAndCode(facilityId, "L1")).isTrue();
        assertThat(levelRepository.existsByFacilityIdAndCode(facilityId, "UNKNOWN")).isFalse();
        assertThat(levelRepository.existsByFacilityIdAndCode(otherFacilityId, "L2")).isFalse();
    }

    private ParkingFacility persistFacility(String name) {
        return facilityRepository.saveAndFlush(new ParkingFacility(
                name,
                name + " address",
                null,
                null
        ));
    }
}
