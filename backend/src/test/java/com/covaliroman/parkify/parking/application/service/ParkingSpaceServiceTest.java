package com.covaliroman.parkify.parking.application.service;

import com.covaliroman.parkify.parking.application.dto.CreateParkingSpaceCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingSpaceResult;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelNotFoundException;
import com.covaliroman.parkify.parking.application.exception.ParkingSpaceCodeAlreadyExistsException;
import com.covaliroman.parkify.parking.application.exception.ParkingSpaceNotFoundException;
import com.covaliroman.parkify.parking.domain.ParkingSpace;
import com.covaliroman.parkify.parking.domain.ParkingSpaceStatus;
import com.covaliroman.parkify.parking.domain.ParkingSpaceType;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingLevelRepository;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingSpaceRepository;
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
class ParkingSpaceServiceTest {

    @Mock
    private ParkingLevelRepository levelRepository;

    @Mock
    private ParkingSpaceRepository spaceRepository;

    @InjectMocks
    private ParkingSpaceService service;

    @Test
    void shouldCreateParkingSpaceForExistingLevel() {
        var levelId = UUID.randomUUID();
        when(levelRepository.existsById(levelId)).thenReturn(true);
        when(spaceRepository.save(any(ParkingSpace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var command = new CreateParkingSpaceCommand(
                levelId,
                " A-01 ",
                ParkingSpaceType.ELECTRIC
        );

        var result = service.create(command);

        assertThat(result.levelId()).isEqualTo(levelId);
        assertThat(result.code()).isEqualTo("A-01");
        assertThat(result.type()).isEqualTo(ParkingSpaceType.ELECTRIC);
        assertThat(result.status()).isEqualTo(ParkingSpaceStatus.ACTIVE);
        assertThat(result.reservable()).isTrue();
    }

    @Test
    void shouldRejectSpaceWhenLevelDoesNotExist() {
        var levelId = UUID.randomUUID();
        when(levelRepository.existsById(levelId)).thenReturn(false);
        var command = new CreateParkingSpaceCommand(
                levelId,
                "A-01",
                ParkingSpaceType.STANDARD
        );

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ParkingLevelNotFoundException.class)
                .hasMessage("Parking level not found: " + levelId);
        verify(spaceRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateSpaceCodeOnLevel() {
        var levelId = UUID.randomUUID();
        when(levelRepository.existsById(levelId)).thenReturn(true);
        when(spaceRepository.existsByLevelIdAndCode(levelId, "A-01"))
                .thenReturn(true);
        var command = new CreateParkingSpaceCommand(
                levelId,
                "A-01",
                ParkingSpaceType.STANDARD
        );

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(ParkingSpaceCodeAlreadyExistsException.class)
                .hasMessage("Parking space code already exists on level "
                        + levelId + ": A-01");
        verify(spaceRepository, never()).save(any());
    }

    @Test
    void shouldReturnSpacesForLevel() {
        var levelId = UUID.randomUUID();
        when(spaceRepository.findAllByLevelId(levelId)).thenReturn(List.of(
                space(levelId, "A-01"),
                space(levelId, "A-02")
        ));

        var result = service.getByLevelId(levelId);

        assertThat(result)
                .extracting(ParkingSpaceResult::code)
                .containsExactly("A-01", "A-02");
    }

    @Test
    void shouldChangeParkingSpaceType() {
        var spaceId = UUID.randomUUID();
        var space = space(UUID.randomUUID(), "A-01");
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(spaceRepository.save(space)).thenReturn(space);

        var result = service.changeType(spaceId, ParkingSpaceType.DISABLED);

        verify(spaceRepository).save(space);
        assertThat(result.type()).isEqualTo(ParkingSpaceType.DISABLED);
    }

    @Test
    void shouldMarkParkingSpaceOutOfService() {
        var spaceId = UUID.randomUUID();
        var space = space(UUID.randomUUID(), "A-01");
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(spaceRepository.save(space)).thenReturn(space);

        var result = service.changeStatus(
                spaceId,
                ParkingSpaceStatus.OUT_OF_SERVICE
        );

        assertThat(result.status()).isEqualTo(ParkingSpaceStatus.OUT_OF_SERVICE);
        assertThat(result.reservable()).isFalse();
    }

    @Test
    void shouldFailWhenParkingSpaceDoesNotExist() {
        var spaceId = UUID.randomUUID();
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(spaceId))
                .isInstanceOf(ParkingSpaceNotFoundException.class)
                .hasMessage("Parking space not found: " + spaceId);
    }

    private ParkingSpace space(UUID levelId, String code) {
        return new ParkingSpace(levelId, code, ParkingSpaceType.STANDARD);
    }
}
