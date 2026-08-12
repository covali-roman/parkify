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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class ParkingSpaceService {

    private final ParkingLevelRepository levelRepository;
    private final ParkingSpaceRepository spaceRepository;

    public ParkingSpaceService(
            ParkingLevelRepository levelRepository,
            ParkingSpaceRepository spaceRepository
    ) {
        this.levelRepository = levelRepository;
        this.spaceRepository = spaceRepository;
    }

    public ParkingSpaceResult create(CreateParkingSpaceCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        var levelId = Objects.requireNonNull(
                command.levelId(),
                "levelId must not be null"
        );
        if (!levelRepository.existsById(levelId)) {
            throw new ParkingLevelNotFoundException(levelId);
        }

        var space = new ParkingSpace(levelId, command.code(), command.type());
        if (spaceRepository.existsByLevelIdAndCode(levelId, space.getCode())) {
            throw new ParkingSpaceCodeAlreadyExistsException(
                    levelId,
                    space.getCode()
            );
        }

        return ParkingSpaceResult.from(spaceRepository.save(space));
    }

    @Transactional(readOnly = true)
    public ParkingSpaceResult getById(UUID spaceId) {
        return ParkingSpaceResult.from(findSpace(spaceId));
    }

    @Transactional(readOnly = true)
    public List<ParkingSpaceResult> getByLevelId(UUID levelId) {
        Objects.requireNonNull(levelId, "levelId must not be null");

        return spaceRepository.findAllByLevelId(levelId).stream()
                .map(ParkingSpaceResult::from)
                .toList();
    }

    public ParkingSpaceResult changeType(
            UUID spaceId,
            ParkingSpaceType type
    ) {
        Objects.requireNonNull(type, "type must not be null");

        var space = findSpace(spaceId);
        space.changeType(type);

        return ParkingSpaceResult.from(spaceRepository.save(space));
    }

    public ParkingSpaceResult changeStatus(
            UUID spaceId,
            ParkingSpaceStatus status
    ) {
        Objects.requireNonNull(status, "status must not be null");

        var space = findSpace(spaceId);
        switch (status) {
            case ACTIVE -> space.activate();
            case OUT_OF_SERVICE -> space.markOutOfService();
        }

        return ParkingSpaceResult.from(spaceRepository.save(space));
    }

    private ParkingSpace findSpace(UUID spaceId) {
        Objects.requireNonNull(spaceId, "spaceId must not be null");

        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ParkingSpaceNotFoundException(spaceId));
    }
}
