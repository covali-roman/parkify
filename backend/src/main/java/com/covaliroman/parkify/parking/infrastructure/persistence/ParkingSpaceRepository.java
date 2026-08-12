package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParkingSpaceRepository
        extends JpaRepository<ParkingSpace, UUID> {

    List<ParkingSpace> findAllByLevelId(UUID levelId);

    boolean existsByLevelIdAndCode(UUID levelId, String code);
}