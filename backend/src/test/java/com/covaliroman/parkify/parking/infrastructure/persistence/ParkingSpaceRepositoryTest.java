package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingLevel;
import com.covaliroman.parkify.parking.domain.ParkingSpace;
import com.covaliroman.parkify.parking.domain.ParkingSpaceStatus;
import com.covaliroman.parkify.parking.domain.ParkingSpaceType;
import com.covaliroman.parkify.support.PostgresTestContainerConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PostgresTestContainerConfig.class)
class ParkingSpaceRepositoryTest {

    @Autowired
    private ParkingFacilityRepository facilityRepository;

    @Autowired
    private ParkingLevelRepository levelRepository;

    @Autowired
    private ParkingSpaceRepository spaceRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistAndLoadParkingSpace() {
        var levelId = persistLevel("Central Parking", "L1").getId();
        var space = new ParkingSpace(levelId, "A-01", ParkingSpaceType.ELECTRIC);
        space.markOutOfService();

        var saved = spaceRepository.saveAndFlush(space);
        var spaceId = saved.getId();
        entityManager.clear();

        assertThat(spaceRepository.findById(spaceId)).isPresent().get().satisfies(actual -> {
            assertThat(actual.getLevelId()).isEqualTo(levelId);
            assertThat(actual.getCode()).isEqualTo("A-01");
            assertThat(actual.getType()).isEqualTo(ParkingSpaceType.ELECTRIC);
            assertThat(actual.getStatus()).isEqualTo(ParkingSpaceStatus.OUT_OF_SERVICE);
            assertThat(actual.getCreatedAt()).isNotNull();
            assertThat(actual.getUpdatedAt()).isNotNull();
        });
    }

    @Test
    void shouldFindOnlySpacesOnRequestedLevelAndCheckCodeExistence() {
        var firstLevelId = persistLevel("Central Parking", "L1").getId();
        var secondLevelId = persistLevel("Airport Parking", "L2").getId();
        spaceRepository.saveAllAndFlush(java.util.List.of(
                new ParkingSpace(firstLevelId, "A-01", ParkingSpaceType.STANDARD),
                new ParkingSpace(firstLevelId, "A-02", ParkingSpaceType.DISABLED),
                new ParkingSpace(secondLevelId, "A-01", ParkingSpaceType.MOTORCYCLE)
        ));
        entityManager.clear();

        var spaces = spaceRepository.findAllByLevelId(firstLevelId);

        assertThat(spaces)
                .extracting(ParkingSpace::getCode)
                .containsExactlyInAnyOrder("A-01", "A-02");
        assertThat(spaceRepository.existsByLevelIdAndCode(firstLevelId, "A-02")).isTrue();
        assertThat(spaceRepository.existsByLevelIdAndCode(firstLevelId, "UNKNOWN")).isFalse();
        assertThat(spaceRepository.existsByLevelIdAndCode(secondLevelId, "A-02")).isFalse();
    }

    private ParkingLevel persistLevel(String facilityName, String levelCode) {
        var facility = facilityRepository.saveAndFlush(new ParkingFacility(
                facilityName,
                facilityName + " address",
                null,
                null
        ));
        return levelRepository.saveAndFlush(new ParkingLevel(
                facility.getId(),
                levelCode,
                levelCode + " display name",
                1
        ));
    }
}
