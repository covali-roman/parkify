package com.covaliroman.parkify.parking.application.dto;

import com.covaliroman.parkify.parking.domain.ParkingSpace;
import com.covaliroman.parkify.parking.domain.ParkingSpaceStatus;
import com.covaliroman.parkify.parking.domain.ParkingSpaceType;

import java.time.Instant;
import java.util.UUID;

public record ParkingSpaceResult(
        UUID id,
        UUID levelId,
        String code,
        ParkingSpaceType type,
        ParkingSpaceStatus status,
        boolean reservable,
        Instant createdAt,
        Instant updatedAt
) {

    public static ParkingSpaceResult from(ParkingSpace space) {
        return new ParkingSpaceResult(
                space.getId(),
                space.getLevelId(),
                space.getCode(),
                space.getType(),
                space.getStatus(),
                space.isReservable(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }
}
