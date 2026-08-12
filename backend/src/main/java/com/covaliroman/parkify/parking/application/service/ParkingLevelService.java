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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class ParkingLevelService {

    private final ParkingFacilityRepository facilityRepository;
    private final ParkingLevelRepository levelRepository;

    public ParkingLevelService(
            ParkingFacilityRepository facilityRepository,
            ParkingLevelRepository levelRepository
    ) {
        this.facilityRepository = facilityRepository;
        this.levelRepository = levelRepository;
    }

    public ParkingLevelResult create(CreateParkingLevelCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        var facilityId = Objects.requireNonNull(
                command.facilityId(),
                "facilityId must not be null"
        );
        if (!facilityRepository.existsById(facilityId)) {
            throw new ParkingFacilityNotFoundException(facilityId);
        }

        var level = new ParkingLevel(
                facilityId,
                command.code(),
                command.displayName(),
                command.sortOrder()
        );
        if (levelRepository.existsByFacilityIdAndCode(facilityId, level.getCode())) {
            throw new ParkingLevelCodeAlreadyExistsException(
                    facilityId,
                    level.getCode()
            );
        }

        return ParkingLevelResult.from(levelRepository.save(level));
    }

    @Transactional(readOnly = true)
    public ParkingLevelResult getById(UUID levelId) {
        return ParkingLevelResult.from(findLevel(levelId));
    }

    @Transactional(readOnly = true)
    public List<ParkingLevelResult> getByFacilityId(UUID facilityId) {
        Objects.requireNonNull(facilityId, "facilityId must not be null");

        return levelRepository.findAllByFacilityIdOrderBySortOrderAsc(facilityId)
                .stream()
                .map(ParkingLevelResult::from)
                .toList();
    }

    public ParkingLevelResult update(
            UUID levelId,
            UpdateParkingLevelCommand command
    ) {
        Objects.requireNonNull(command, "command must not be null");

        var level = findLevel(levelId);
        level.rename(command.displayName());
        level.changeSortOrder(command.sortOrder());

        return ParkingLevelResult.from(levelRepository.save(level));
    }

    private ParkingLevel findLevel(UUID levelId) {
        Objects.requireNonNull(levelId, "levelId must not be null");

        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ParkingLevelNotFoundException(levelId));
    }
}
