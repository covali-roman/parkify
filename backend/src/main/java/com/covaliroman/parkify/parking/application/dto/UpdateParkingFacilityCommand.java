package com.covaliroman.parkify.parking.application.dto;

import java.math.BigDecimal;

public record UpdateParkingFacilityCommand(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude
) {
}
