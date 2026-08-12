package com.covaliroman.parkify.parking.application.dto;

import java.util.UUID;

public record CreateParkingLevelCommand(
        UUID facilityId,
        String code,
        String displayName,
        int sortOrder
) {
}
