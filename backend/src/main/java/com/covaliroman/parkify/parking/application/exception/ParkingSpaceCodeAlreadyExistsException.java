package com.covaliroman.parkify.parking.application.exception;

import java.util.UUID;

public class ParkingSpaceCodeAlreadyExistsException extends RuntimeException {

    public ParkingSpaceCodeAlreadyExistsException(UUID levelId, String code) {
        super("Parking space code already exists on level "
                + levelId + ": " + code);
    }
}
