package com.covaliroman.parkify.parking.web.dto;

import com.covaliroman.parkify.parking.domain.ParkingSpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateParkingSpaceRequest(
        @NotBlank @Size(max = 30) String code,
        @NotNull ParkingSpaceType type
) {
}
