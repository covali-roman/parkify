package com.covaliroman.parkify.parking.application.service;

import com.covaliroman.parkify.parking.application.dto.CreateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingFacilityResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.exception.ParkingFacilityNotFoundException;
import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;
import com.covaliroman.parkify.parking.infrastructure.persistence.ParkingFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class ParkingFacilityService {

    private final ParkingFacilityRepository repository;

    public ParkingFacilityService(ParkingFacilityRepository repository) {
        this.repository = repository;
    }

    public ParkingFacilityResult create(CreateParkingFacilityCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        var facility = new ParkingFacility(
                command.name(),
                command.address(),
                command.latitude(),
                command.longitude()
        );

        return ParkingFacilityResult.from(repository.save(facility));
    }

    @Transactional(readOnly = true)
    public ParkingFacilityResult getById(UUID facilityId) {
        return ParkingFacilityResult.from(findFacility(facilityId));
    }

    @Transactional(readOnly = true)
    public List<ParkingFacilityResult> getAll() {
        return repository.findAll().stream()
                .map(ParkingFacilityResult::from)
                .toList();
    }

    public ParkingFacilityResult update(
            UUID facilityId,
            UpdateParkingFacilityCommand command
    ) {
        Objects.requireNonNull(command, "command must not be null");

        var facility = findFacility(facilityId);
        facility.rename(command.name());
        facility.changeAddress(command.address());
        facility.changeCoordinates(command.latitude(), command.longitude());

        return ParkingFacilityResult.from(repository.save(facility));
    }

    public ParkingFacilityResult changeStatus(
            UUID facilityId,
            ParkingFacilityStatus status
    ) {
        Objects.requireNonNull(status, "status must not be null");

        var facility = findFacility(facilityId);
        switch (status) {
            case ACTIVE -> facility.activate();
            case INACTIVE -> facility.deactivate();
        }

        return ParkingFacilityResult.from(repository.save(facility));
    }

    private ParkingFacility findFacility(UUID facilityId) {
        Objects.requireNonNull(facilityId, "facilityId must not be null");

        return repository.findById(facilityId)
                .orElseThrow(() -> new ParkingFacilityNotFoundException(facilityId));
    }
}
