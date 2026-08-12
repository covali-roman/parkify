package com.covaliroman.parkify.parking.application.dto;

import com.covaliroman.parkify.parking.domain.ParkingFacility;
import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ParkingFacilityResult(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        ParkingFacilityStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static ParkingFacilityResult from(ParkingFacility facility) {
        return new ParkingFacilityResult(
                facility.getId(),
                facility.getName(),
                facility.getAddress(),
                facility.getLatitude(),
                facility.getLongitude(),
                facility.getStatus(),
                facility.getCreatedAt(),
                facility.getUpdatedAt()
        );
    }
}
