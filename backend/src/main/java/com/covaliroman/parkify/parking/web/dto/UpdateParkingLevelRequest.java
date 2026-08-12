package com.covaliroman.parkify.parking.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateParkingLevelRequest(
        @Size(max = 100)
        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        String displayName,
        int sortOrder
) {
}
