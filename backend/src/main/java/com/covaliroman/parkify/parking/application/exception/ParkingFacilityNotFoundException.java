package com.covaliroman.parkify.parking.application.exception;

import java.util.UUID;

public class ParkingFacilityNotFoundException extends RuntimeException {

    public ParkingFacilityNotFoundException(UUID facilityId) {
        super("Parking facility not found: " + facilityId);
    }
}
