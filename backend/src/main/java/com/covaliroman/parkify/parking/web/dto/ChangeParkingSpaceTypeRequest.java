package com.covaliroman.parkify.parking.web.dto;

import com.covaliroman.parkify.parking.domain.ParkingSpaceType;
import jakarta.validation.constraints.NotNull;

public record ChangeParkingSpaceTypeRequest(
        @NotNull ParkingSpaceType type
) {
}
