package com.covaliroman.parkify.parking.web;

import com.covaliroman.parkify.parking.application.dto.CreateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingLevelResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.service.ParkingLevelService;
import com.covaliroman.parkify.parking.web.dto.CreateParkingLevelRequest;
import com.covaliroman.parkify.parking.web.dto.UpdateParkingLevelRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.covaliroman.parkify.config.OpenApiConfig.LEVELS_TAG;

@RestController
@RequestMapping("/api")
@Tag(name = LEVELS_TAG)
public class ParkingLevelController {

    private final ParkingLevelService service;

    public ParkingLevelController(ParkingLevelService service) {
        this.service = service;
    }

    @PostMapping("/parking-facilities/{facilityId}/levels")
    public ResponseEntity<ParkingLevelResult> create(
            @PathVariable UUID facilityId,
            @Valid @RequestBody CreateParkingLevelRequest request
    ) {
        var result = service.create(new CreateParkingLevelCommand(
                facilityId,
                request.code(),
                request.displayName(),
                request.sortOrder()
        ));

        return ResponseEntity
                .created(URI.create("/api/parking-levels/" + result.id()))
                .body(result);
    }

    @GetMapping("/parking-facilities/{facilityId}/levels")
    public List<ParkingLevelResult> getByFacilityId(
            @PathVariable UUID facilityId
    ) {
        return service.getByFacilityId(facilityId);
    }

    @GetMapping("/parking-levels/{levelId}")
    public ParkingLevelResult getById(@PathVariable UUID levelId) {
        return service.getById(levelId);
    }

    @PutMapping("/parking-levels/{levelId}")
    public ParkingLevelResult update(
            @PathVariable UUID levelId,
            @Valid @RequestBody UpdateParkingLevelRequest request
    ) {
        return service.update(levelId, new UpdateParkingLevelCommand(
                request.displayName(),
                request.sortOrder()
        ));
    }
}
