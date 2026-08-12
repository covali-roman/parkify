package com.covaliroman.parkify.parking.application.dto;

import com.covaliroman.parkify.parking.domain.ParkingLevel;

import java.time.Instant;
import java.util.UUID;

public record ParkingLevelResult(
        UUID id,
        UUID facilityId,
        String code,
        String displayName,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt
) {

    public static ParkingLevelResult from(ParkingLevel level) {
        return new ParkingLevelResult(
                level.getId(),
                level.getFacilityId(),
                level.getCode(),
                level.getDisplayName(),
                level.getSortOrder(),
                level.getCreatedAt(),
                level.getUpdatedAt()
        );
    }
}
