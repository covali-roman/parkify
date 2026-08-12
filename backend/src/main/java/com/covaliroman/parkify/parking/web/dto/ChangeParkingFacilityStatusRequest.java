package com.covaliroman.parkify.parking.web.dto;

import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeParkingFacilityStatusRequest(
        @NotNull ParkingFacilityStatus status
) {
}
