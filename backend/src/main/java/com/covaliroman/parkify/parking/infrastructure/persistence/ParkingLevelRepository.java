package com.covaliroman.parkify.parking.infrastructure.persistence;

import com.covaliroman.parkify.parking.domain.ParkingLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParkingLevelRepository
        extends JpaRepository<ParkingLevel, UUID> {

    List<ParkingLevel> findAllByFacilityIdOrderBySortOrderAsc(UUID facilityId);

    boolean existsByFacilityIdAndCode(UUID facilityId, String code);
}