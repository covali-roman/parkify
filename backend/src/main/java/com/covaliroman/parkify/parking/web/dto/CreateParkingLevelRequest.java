package com.covaliroman.parkify.parking.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateParkingLevelRequest(
        @NotBlank @Size(max = 30) String code,
        @Size(max = 100)
        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        String displayName,
        int sortOrder
) {
}
