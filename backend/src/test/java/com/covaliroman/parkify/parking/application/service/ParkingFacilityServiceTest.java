package com.covaliroman.parkify.parking.application.service;

import com.covaliroman.parkify.parking.application.dto.CreateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingFacilityResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.exception.ParkingFacilityNotFoundException;
import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingFacilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingFacilityServiceTest {

    @Mock
    private ParkingFacilityRepository repository;

    @InjectMocks
    private ParkingFacilityService service;

    @Test
    void shouldCreateParkingFacility() {
        when(repository.save(any(ParkingFacility.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var command = new CreateParkingFacilityCommand(
                " Central Parking ",
                " Main Street 10 ",
                new BigDecimal("47.010500"),
                new BigDecimal("28.863800")
        );

        var result = service.create(command);

        var captor = ArgumentCaptor.forClass(ParkingFacility.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Central Parking");
        assertThat(result.name()).isEqualTo("Central Parking");
        assertThat(result.address()).isEqualTo("Main Street 10");
        assertThat(result.status()).isEqualTo(ParkingFacilityStatus.ACTIVE);
        assertThat(result.latitude()).isEqualByComparingTo("47.010500");
        assertThat(result.longitude()).isEqualByComparingTo("28.863800");
    }

    @Test
    void shouldReturnParkingFacilityById() {
        var facilityId = UUID.randomUUID();
        var facility = facility("Central Parking");
        when(repository.findById(facilityId)).thenReturn(Optional.of(facility));

        var result = service.getById(facilityId);

        assertThat(result.name()).isEqualTo("Central Parking");
        assertThat(result.status()).isEqualTo(ParkingFacilityStatus.ACTIVE);
    }

    @Test
    void shouldReturnAllParkingFacilities() {
        when(repository.findAll()).thenReturn(List.of(
                facility("Central Parking"),
                facility("Airport Parking")
        ));

        var result = service.getAll();

        assertThat(result)
                .extracting(ParkingFacilityResult::name)
                .containsExactly("Central Parking", "Airport Parking");
    }

    @Test
    void shouldUpdateParkingFacility() {
        var facilityId = UUID.randomUUID();
        var facility = facility("Old Name");
        when(repository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(repository.save(facility)).thenReturn(facility);
        var command = new UpdateParkingFacilityCommand(
                "New Name",
                "New Address",
                null,
                null
        );

        var result = service.update(facilityId, command);

        verify(repository).save(facility);
        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.address()).isEqualTo("New Address");
        assertThat(result.latitude()).isNull();
        assertThat(result.longitude()).isNull();
    }

    @Test
    void shouldChangeParkingFacilityStatus() {
        var facilityId = UUID.randomUUID();
        var facility = facility("Central Parking");
        when(repository.findById(facilityId)).thenReturn(Optional.of(facility));
        when(repository.save(facility)).thenReturn(facility);

        var result = service.changeStatus(facilityId, ParkingFacilityStatus.INACTIVE);

        verify(repository).save(facility);
        assertThat(result.status()).isEqualTo(ParkingFacilityStatus.INACTIVE);
    }

    @Test
    void shouldFailWhenParkingFacilityDoesNotExist() {
        var facilityId = UUID.randomUUID();
        when(repository.findById(facilityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(facilityId))
                .isInstanceOf(ParkingFacilityNotFoundException.class)
                .hasMessage("Parking facility not found: " + facilityId);
    }

    private ParkingFacility facility(String name) {
        return new ParkingFacility(name, name + " address", null, null);
    }
}
