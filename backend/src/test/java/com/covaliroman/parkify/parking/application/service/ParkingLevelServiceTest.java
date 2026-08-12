package com.covaliroman.parkify.parking.application.service;

import com.covaliroman.parkify.parking.application.dto.CreateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingLevelResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.exception.ParkingFacilityNotFoundException;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelCodeAlreadyExistsException;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelNotFoundException;
import com.covaliroman.parkify.parking.domain.ParkingLevel;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingFacilityRepository;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingLevelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLevelServiceTest {

    @Mock
    private ParkingFacilityRepository facilityRepository;

    @Mock
    private ParkingLevelRepository levelRepository;

    @InjectMocks
    private ParkingLevelService service;

    @Test
    void shouldCreateParkingLevelForExistingFacility() {
        var facilityId = UUID.randomUUID();
        when(facilityRepository.existsById(facilityId)).thenReturn(true);
        when(levelRepository.save(any(ParkingLevel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var command = new CreateParkingLevelCommand(
                facilityId,
                " L1 ",
                " First Level ",
                10
        );

        var result = service.create(command);

        assertThat(result.facilityId()).isEqualTo(facilityId);
        assertThat(result.code()).isEqualTo("L1");
        assertThat(result.displayName()).isEqualTo("First Level");
        assertThat(result.sortOrder()).isEqualTo(10);
    }

    @Test
    void shouldRejectLevelWhenFacilityDoesNotExist() {
        var facilityId = UUID.randomUUID();
        when(facilityRepository.existsById(facilityId)).thenReturn(false);
        var command = new CreateParkingLevelCommand(facilityId, "L1", null, 1);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ParkingFacilityNotFoundException.class)
                .hasMessage("Parking facility not found: " + facilityId);
        verify(levelRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateLevelCodeWithinFacility() {
        var facilityId = UUID.randomUUID();
        when(facilityRepository.existsById(facilityId)).thenReturn(true);
        when(levelRepository.existsByFacilityIdAndCode(facilityId, "L1"))
                .thenReturn(true);
        var command = new CreateParkingLevelCommand(facilityId, "L1", null, 1);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ParkingLevelCodeAlreadyExistsException.class)
                .hasMessage("Parking level code already exists in facility "
                        + facilityId + ": L1");
        verify(levelRepository, never()).save(any());
    }

    @Test
    void shouldReturnFacilityLevelsInRepositoryOrder() {
        var facilityId = UUID.randomUUID();
        when(levelRepository.findAllByFacilityIdOrderBySortOrderAsc(facilityId))
                .thenReturn(List.of(
                        level(facilityId, "L1", 10),
                        level(facilityId, "L2", 20)
                ));

        var result = service.getByFacilityId(facilityId);

        assertThat(result)
                .extracting(ParkingLevelResult::code)
                .containsExactly("L1", "L2");
    }

    @Test
    void shouldUpdateParkingLevel() {
        var levelId = UUID.randomUUID();
        var level = level(UUID.randomUUID(), "L1", 10);
        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(levelRepository.save(level)).thenReturn(level);

        var result = service.update(
                levelId,
                new UpdateParkingLevelCommand("Main Floor", 5)
        );

        verify(levelRepository).save(level);
        assertThat(result.displayName()).isEqualTo("Main Floor");
        assertThat(result.sortOrder()).isEqualTo(5);
    }

    @Test
    void shouldFailWhenParkingLevelDoesNotExist() {
        var levelId = UUID.randomUUID();
        when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(levelId))
                .isInstanceOf(ParkingLevelNotFoundException.class)
                .hasMessage("Parking level not found: " + levelId);
    }

    private ParkingLevel level(UUID facilityId, String code, int sortOrder) {
        return new ParkingLevel(facilityId, code, code + " display name", sortOrder);
    }
}
