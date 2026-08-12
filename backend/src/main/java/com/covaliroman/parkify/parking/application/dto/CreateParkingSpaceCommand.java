package com.covaliroman.parkify.parking.application.dto;

import com.covaliroman.parkify.parking.domain.ParkingSpaceType;

import java.util.UUID;

public record CreateParkingSpaceCommand(
        UUID levelId,
        String code,
        ParkingSpaceType type
) {
}
