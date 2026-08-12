package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParkingFacilityRepository
        extends JpaRepository<ParkingFacility, UUID> {
}