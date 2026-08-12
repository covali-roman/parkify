package com.covaliroman.parkify.parking.web;

import com.covaliroman.parkify.parking.application.dto.CreateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingFacilityResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.service.ParkingFacilityService;
import com.covaliroman.parkify.parking.web.dto.ChangeParkingFacilityStatusRequest;
import com.covaliroman.parkify.parking.web.dto.CreateParkingFacilityRequest;
import com.covaliroman.parkify.parking.web.dto.UpdateParkingFacilityRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parking-facilities")
public class ParkingFacilityController {

    private final ParkingFacilityService service;

    public ParkingFacilityController(ParkingFacilityService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParkingFacilityResult> create(
            @Valid @RequestBody CreateParkingFacilityRequest request
    ) {
        var result = service.create(new CreateParkingFacilityCommand(
                request.name(),
                request.address(),
                request.latitude(),
                request.longitude()
        ));

        return ResponseEntity
                .created(URI.create("/api/parking-facilities/" + result.id()))
                .body(result);
    }

    @GetMapping("/{facilityId}")
    public ParkingFacilityResult getById(@PathVariable UUID facilityId) {
        return service.getById(facilityId);
    }

    @GetMapping
    public List<ParkingFacilityResult> getAll() {
        return service.getAll();
    }

    @PutMapping("/{facilityId}")
    public ParkingFacilityResult update(
            @PathVariable UUID facilityId,
            @Valid @RequestBody UpdateParkingFacilityRequest request
    ) {
        return service.update(facilityId, new UpdateParkingFacilityCommand(
                request.name(),
                request.address(),
                request.latitude(),
                request.longitude()
        ));
    }

    @PatchMapping("/{facilityId}/status")
    public ParkingFacilityResult changeStatus(
            @PathVariable UUID facilityId,
            @Valid @RequestBody ChangeParkingFacilityStatusRequest request
    ) {
        return service.changeStatus(facilityId, request.status());
    }
}
