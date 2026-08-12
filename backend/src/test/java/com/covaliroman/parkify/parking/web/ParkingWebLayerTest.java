package com.covaliroman.parkify.parking.web;

import com.covaliroman.parkify.parking.application.dto.CreateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.dto.CreateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.dto.CreateParkingSpaceCommand;
import com.covaliroman.parkify.parking.application.dto.ParkingFacilityResult;
import com.covaliroman.parkify.parking.application.dto.ParkingLevelResult;
import com.covaliroman.parkify.parking.application.dto.ParkingSpaceResult;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingFacilityCommand;
import com.covaliroman.parkify.parking.application.dto.UpdateParkingLevelCommand;
import com.covaliroman.parkify.parking.application.exception.ParkingFacilityNotFoundException;
import com.covaliroman.parkify.parking.application.exception.ParkingLevelCodeAlreadyExistsException;
import com.covaliroman.parkify.parking.application.service.ParkingFacilityService;
import com.covaliroman.parkify.parking.application.service.ParkingLevelService;
import com.covaliroman.parkify.parking.application.service.ParkingSpaceService;
import com.covaliroman.parkify.config.OpenApiConfig;
import com.covaliroman.parkify.parking.domain.ParkingFacilityStatus;
import com.covaliroman.parkify.parking.domain.ParkingSpaceStatus;
import com.covaliroman.parkify.parking.domain.ParkingSpaceType;
import com.covaliroman.parkify.parking.web.error.ApiExceptionHandler;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        ParkingFacilityController.class,
        ParkingLevelController.class,
        ParkingSpaceController.class
})
@Import({ApiExceptionHandler.class, OpenApiConfig.class})
class ParkingWebLayerTest {

    private static final Instant NOW = Instant.parse("2026-08-12T08:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenAPI openApi;

    @MockitoBean
    private ParkingFacilityService facilityService;

    @MockitoBean
    private ParkingLevelService levelService;

    @MockitoBean
    private ParkingSpaceService spaceService;

    @Test
    void shouldConfigureGlobalOpenApiDocumentation() {
        assertThat(openApi.getInfo().getTitle()).isEqualTo("Parkify API");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("v1");
        assertThat(openApi.getTags())
                .extracting(io.swagger.v3.oas.models.tags.Tag::getName)
                .containsExactly(
                        "Parking facilities",
                        "Parking levels",
                        "Parking spaces"
                );
    }

    @Test
    void shouldCreateParkingFacility() throws Exception {
        var facilityId = UUID.randomUUID();
        when(facilityService.create(any())).thenReturn(facilityResult(facilityId));

        mockMvc.perform(post("/api/parking-facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Central Parking",
                                  "address": "Main Street 10",
                                  "latitude": 47.010500,
                                  "longitude": 28.863800
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/parking-facilities/" + facilityId
                ))
                .andExpect(jsonPath("$.id").value(facilityId.toString()))
                .andExpect(jsonPath("$.name").value("Central Parking"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        var captor = ArgumentCaptor.forClass(CreateParkingFacilityCommand.class);
        verify(facilityService).create(captor.capture());
        assertThat(captor.getValue().latitude()).isEqualByComparingTo("47.010500");
    }

    @Test
    void shouldGetAndListParkingFacilities() throws Exception {
        var facilityId = UUID.randomUUID();
        var result = facilityResult(facilityId);
        when(facilityService.getById(facilityId)).thenReturn(result);
        when(facilityService.getAll()).thenReturn(List.of(result));

        mockMvc.perform(get("/api/parking-facilities/{facilityId}", facilityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(facilityId.toString()));
        mockMvc.perform(get("/api/parking-facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Central Parking"));
    }

    @Test
    void shouldUpdateFacilityAndChangeItsStatus() throws Exception {
        var facilityId = UUID.randomUUID();
        var active = facilityResult(facilityId);
        var inactive = new ParkingFacilityResult(
                active.id(), active.name(), active.address(), active.latitude(),
                active.longitude(), ParkingFacilityStatus.INACTIVE,
                active.createdAt(), active.updatedAt()
        );
        when(facilityService.update(any(), any())).thenReturn(active);
        when(facilityService.changeStatus(
                facilityId,
                ParkingFacilityStatus.INACTIVE
        )).thenReturn(inactive);

        mockMvc.perform(put("/api/parking-facilities/{facilityId}", facilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Central Parking",
                                  "address": "Main Street 10",
                                  "latitude": null,
                                  "longitude": null
                                }
                                """))
                .andExpect(status().isOk());
        verify(facilityService).update(
                facilityId,
                new UpdateParkingFacilityCommand(
                        "Central Parking",
                        "Main Street 10",
                        null,
                        null
                )
        );

        mockMvc.perform(patch("/api/parking-facilities/{facilityId}/status", facilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void shouldReturnBadRequestForInvalidFacility() throws Exception {
        mockMvc.perform(post("/api/parking-facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "address": "Main Street 10",
                                  "latitude": 47.010500,
                                  "longitude": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.violations.name").exists());

        verify(facilityService, never()).create(any());
    }

    @Test
    void shouldReturnNotFoundForMissingFacility() throws Exception {
        var facilityId = UUID.randomUUID();
        when(facilityService.getById(facilityId))
                .thenThrow(new ParkingFacilityNotFoundException(facilityId));

        mockMvc.perform(get("/api/parking-facilities/{facilityId}", facilityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(
                        "Parking facility not found: " + facilityId
                ));
    }

    @Test
    void shouldCreateParkingLevel() throws Exception {
        var facilityId = UUID.randomUUID();
        var levelId = UUID.randomUUID();
        when(levelService.create(any())).thenReturn(levelResult(levelId, facilityId));

        mockMvc.perform(post(
                        "/api/parking-facilities/{facilityId}/levels",
                        facilityId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "L1",
                                  "displayName": "First Level",
                                  "sortOrder": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/parking-levels/" + levelId
                ))
                .andExpect(jsonPath("$.facilityId").value(facilityId.toString()));

        verify(levelService).create(new CreateParkingLevelCommand(
                facilityId,
                "L1",
                "First Level",
                10
        ));
    }

    @Test
    void shouldGetListAndUpdateParkingLevels() throws Exception {
        var facilityId = UUID.randomUUID();
        var levelId = UUID.randomUUID();
        var result = levelResult(levelId, facilityId);
        when(levelService.getByFacilityId(facilityId)).thenReturn(List.of(result));
        when(levelService.getById(levelId)).thenReturn(result);
        when(levelService.update(levelId, new UpdateParkingLevelCommand("Main", 5)))
                .thenReturn(result);

        mockMvc.perform(get(
                        "/api/parking-facilities/{facilityId}/levels",
                        facilityId
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("L1"));
        mockMvc.perform(get("/api/parking-levels/{levelId}", levelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(levelId.toString()));
        mockMvc.perform(put("/api/parking-levels/{levelId}", levelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"Main\",\"sortOrder\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnConflictForDuplicateLevelCode() throws Exception {
        var facilityId = UUID.randomUUID();
        when(levelService.create(any())).thenThrow(
                new ParkingLevelCodeAlreadyExistsException(facilityId, "L1")
        );

        mockMvc.perform(post(
                        "/api/parking-facilities/{facilityId}/levels",
                        facilityId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"L1\",\"sortOrder\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldCreateParkingSpace() throws Exception {
        var levelId = UUID.randomUUID();
        var spaceId = UUID.randomUUID();
        when(spaceService.create(any())).thenReturn(spaceResult(spaceId, levelId));

        mockMvc.perform(post("/api/parking-levels/{levelId}/spaces", levelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"A-01\",\"type\":\"ELECTRIC\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/parking-spaces/" + spaceId
                ))
                .andExpect(jsonPath("$.type").value("ELECTRIC"));

        verify(spaceService).create(new CreateParkingSpaceCommand(
                levelId,
                "A-01",
                ParkingSpaceType.ELECTRIC
        ));
    }

    @Test
    void shouldGetAndListParkingSpaces() throws Exception {
        var levelId = UUID.randomUUID();
        var spaceId = UUID.randomUUID();
        var result = spaceResult(spaceId, levelId);
        when(spaceService.getByLevelId(levelId)).thenReturn(List.of(result));
        when(spaceService.getById(spaceId)).thenReturn(result);

        mockMvc.perform(get("/api/parking-levels/{levelId}/spaces", levelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("A-01"));
        mockMvc.perform(get("/api/parking-spaces/{spaceId}", spaceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(spaceId.toString()));
    }

    @Test
    void shouldChangeParkingSpaceTypeAndStatus() throws Exception {
        var levelId = UUID.randomUUID();
        var spaceId = UUID.randomUUID();
        var electric = spaceResult(spaceId, levelId);
        var unavailable = new ParkingSpaceResult(
                spaceId,
                levelId,
                "A-01",
                ParkingSpaceType.ELECTRIC,
                ParkingSpaceStatus.OUT_OF_SERVICE,
                false,
                NOW,
                NOW
        );
        when(spaceService.changeType(spaceId, ParkingSpaceType.ELECTRIC))
                .thenReturn(electric);
        when(spaceService.changeStatus(spaceId, ParkingSpaceStatus.OUT_OF_SERVICE))
                .thenReturn(unavailable);

        mockMvc.perform(patch("/api/parking-spaces/{spaceId}/type", spaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"ELECTRIC\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("ELECTRIC"));
        mockMvc.perform(patch("/api/parking-spaces/{spaceId}/status", spaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OUT_OF_SERVICE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservable").value(false));
    }

    @Test
    void shouldReturnBadRequestForUnknownEnumValue() throws Exception {
        mockMvc.perform(post(
                        "/api/parking-levels/{levelId}/spaces",
                        UUID.randomUUID()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"A-01\",\"type\":\"UNKNOWN\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Request body is missing or malformed"
                ));
    }

    private ParkingFacilityResult facilityResult(UUID facilityId) {
        return new ParkingFacilityResult(
                facilityId,
                "Central Parking",
                "Main Street 10",
                new BigDecimal("47.010500"),
                new BigDecimal("28.863800"),
                ParkingFacilityStatus.ACTIVE,
                NOW,
                NOW
        );
    }

    private ParkingLevelResult levelResult(UUID levelId, UUID facilityId) {
        return new ParkingLevelResult(
                levelId,
                facilityId,
                "L1",
                "First Level",
                10,
                NOW,
                NOW
        );
    }

    private ParkingSpaceResult spaceResult(UUID spaceId, UUID levelId) {
        return new ParkingSpaceResult(
                spaceId,
                levelId,
                "A-01",
                ParkingSpaceType.ELECTRIC,
                ParkingSpaceStatus.ACTIVE,
                true,
                NOW,
                NOW
        );
    }
}
