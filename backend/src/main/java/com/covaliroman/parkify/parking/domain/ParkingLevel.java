package com.covaliroman.parkify.parking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "parking_level")
public class ParkingLevel {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "facility_id", nullable = false)
    private UUID facilityId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ParkingLevel() {
        // Required by JPA
    }

    public ParkingLevel(
            UUID facilityId,
            String code,
            String displayName,
            int sortOrder
    ) {
        this.facilityId = Objects.requireNonNull(
                facilityId,
                "facilityId must not be null"
        );

        this.code = requireNotBlank(code, "code");
        this.displayName = normalizeDisplayName(displayName);
        this.sortOrder = sortOrder;

        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void rename(String displayName) {
        this.displayName = normalizeDisplayName(displayName);
        touch();
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
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

    private static String normalizeDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        if (displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "displayName must not be blank when provided"
            );
        }

        return displayName.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getFacilityId() {
        return facilityId;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSortOrder() {
        return sortOrder;
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

        if (!(o instanceof ParkingLevel that)) {
            return false;
        }

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}