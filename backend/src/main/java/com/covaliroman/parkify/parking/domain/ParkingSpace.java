package io.github.username.parkify.parking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "parking_space")
public class ParkingSpace {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "level_id", nullable = false)
    private UUID levelId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ParkingSpaceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ParkingSpaceStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ParkingSpace() {
        // Required by JPA
    }

    public ParkingSpace(
            UUID levelId,
            String code,
            ParkingSpaceType type
    ) {
        this.levelId = Objects.requireNonNull(
                levelId,
                "levelId must not be null"
        );

        this.code = requireNotBlank(code, "code");

        this.type = Objects.requireNonNull(
                type,
                "type must not be null"
        );

        this.status = ParkingSpaceStatus.ACTIVE;

        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void changeType(ParkingSpaceType type) {
        this.type = Objects.requireNonNull(
                type,
                "type must not be null"
        );

        touch();
    }

    public void activate() {
        this.status = ParkingSpaceStatus.ACTIVE;
        touch();
    }

    public void markOutOfService() {
        this.status = ParkingSpaceStatus.OUT_OF_SERVICE;
        touch();
    }

    public boolean isReservable() {
        return status == ParkingSpaceStatus.ACTIVE;
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

    public UUID getId() {
        return id;
    }

    public UUID getLevelId() {
        return levelId;
    }

    public String getCode() {
        return code;
    }

    public ParkingSpaceType getType() {
        return type;
    }

    public ParkingSpaceStatus getStatus() {
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

        if (!(o instanceof ParkingSpace that)) {
            return false;
        }

        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}