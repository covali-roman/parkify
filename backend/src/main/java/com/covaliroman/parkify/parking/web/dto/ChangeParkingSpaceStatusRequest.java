package com.covaliroman.parkify.parking.web.dto;

import com.covaliroman.parkify.parking.domain.ParkingSpaceStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeParkingSpaceStatusRequest(
        @NotNull ParkingSpaceStatus status
) {
}
