package com.covaliroman.parkify.parking.application.exception;

import java.util.UUID;

public class ParkingLevelCodeAlreadyExistsException extends RuntimeException {

    public ParkingLevelCodeAlreadyExistsException(UUID facilityId, String code) {
        super("Parking level code already exists in facility "
                + facilityId + ": " + code);
    }
}
