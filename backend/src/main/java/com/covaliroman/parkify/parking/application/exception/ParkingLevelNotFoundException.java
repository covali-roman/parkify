package com.covaliroman.parkify.parking.application.exception;

import java.util.UUID;

public class ParkingLevelNotFoundException extends RuntimeException {

    public ParkingLevelNotFoundException(UUID levelId) {
        super("Parking level not found: " + levelId);
    }
}
