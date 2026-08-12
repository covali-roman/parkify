package com.covaliroman.parkify.parking.application.exception;

import java.util.UUID;

public class ParkingSpaceNotFoundException extends RuntimeException {

    public ParkingSpaceNotFoundException(UUID spaceId) {
        super("Parking space not found: " + spaceId);
    }
}
