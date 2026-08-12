package com.covaliroman.parkify.parking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "parking_facility")
public class ParkingFacility {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ParkingFacilityStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ParkingFacility() {
        // Required by JPA
    }

    public ParkingFacility(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.name = requireNotBlank(name, "name");
        this.address = requireNotBlank(address, "address");
        validateCoordinates(latitude, longitude);

        this.latitude = latitude;
        this.longitude = longitude;
        this.status = ParkingFacilityStatus.ACTIVE;

        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void rename(String name) {
        this.name = requireNotBlank(name, "name");
        touch();
    }

    public void changeAddress(String address) {
        this.address = requireNotBlank(address, "address");
        touch();
    }

    public void changeCoordinates(
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        validateCoordinates(latitude, longitude);

        this.latitude = latitude;
        this.longitude = longitude;

        touch();
    }

    public void activate() {
        this.status = ParkingFacilityStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = ParkingFacilityStatus.INACTIVE;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value.trim();
    }

    private static void validateCoordinates(
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException(
                    "latitude and longitude must either both be present or both be null"
            );
        }

        if (latitude != null
                && (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new IllegalArgumentException(
                    "latitude must be between -90 and 90"
            );
        }

        if (longitude != null
                && (longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new IllegalArgumentException(
                    "longitude must be between -180 and 180"
            );
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public ParkingFacilityStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ParkingFacility that)) {
            return false;
        }

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}