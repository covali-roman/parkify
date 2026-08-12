package com.covaliroman.parkify.parking.web;

import com.covaliroman.parkify.parking.application.dto.CreateParkingSpaceCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingSpaceResult;
import com.covaliroman.parkify.parking.application.service.ParkingSpaceService;
import com.covaliroman.parkify.parking.web.dto.ChangeParkingSpaceStatusRequest;
import com.covaliroman.parkify.parking.web.dto.ChangeParkingSpaceTypeRequest;
import com.covaliroman.parkify.parking.web.dto.CreateParkingSpaceRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ParkingSpaceController {

    private final ParkingSpaceService service;

    public ParkingSpaceController(ParkingSpaceService service) {
        this.service = service;
    }

    @PostMapping("/parking-levels/{levelId}/spaces")
    public ResponseEntity<ParkingSpaceResult> create(
            @PathVariable UUID levelId,
            @Valid @RequestBody CreateParkingSpaceRequest request
    ) {
        var result = service.create(new CreateParkingSpaceCommand(
                levelId,
                request.code(),
                request.type()
        ));

        return ResponseEntity
                .created(URI.create("/api/parking-spaces/" + result.id()))
                .body(result);
    }

    @GetMapping("/parking-levels/{levelId}/spaces")
    public List<ParkingSpaceResult> getByLevelId(@PathVariable UUID levelId) {
        return service.getByLevelId(levelId);
    }

    @GetMapping("/parking-spaces/{spaceId}")
    public ParkingSpaceResult getById(@PathVariable UUID spaceId) {
        return service.getById(spaceId);
    }

    @PatchMapping("/parking-spaces/{spaceId}/type")
    public ParkingSpaceResult changeType(
            @PathVariable UUID spaceId,
            @Valid @RequestBody ChangeParkingSpaceTypeRequest request
    ) {
        return service.changeType(spaceId, request.type());
    }

    @PatchMapping("/parking-spaces/{spaceId}/status")
    public ParkingSpaceResult changeStatus(
            @PathVariable UUID spaceId,
            @Valid @RequestBody ChangeParkingSpaceStatusRequest request
    ) {
        return service.changeStatus(spaceId, request.status());
    }
}
