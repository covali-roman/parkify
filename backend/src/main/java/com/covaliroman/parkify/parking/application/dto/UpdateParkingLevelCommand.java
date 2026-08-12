package com.covaliroman.parkify.parking.application.dto;

public record UpdateParkingLevelCommand(
        String displayName,
        int sortOrder
) {
}
