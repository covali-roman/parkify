CREATE TABLE parking_facility
(
    id          UUID         NOT NULL,
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(500) NOT NULL,
    latitude    NUMERIC(9,6),
    longitude   NUMERIC(9,6),
    status      VARCHAR(30)  NOT NULL,

    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_parking_facility
        PRIMARY KEY (id),

    CONSTRAINT ck_parking_facility_name_not_blank
        CHECK (BTRIM(name) <> ''),

    CONSTRAINT ck_parking_facility_address_not_blank
        CHECK (BTRIM(address) <> ''),

    CONSTRAINT ck_parking_facility_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),

    CONSTRAINT ck_parking_facility_latitude
        CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),

    CONSTRAINT ck_parking_facility_longitude
        CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),

    CONSTRAINT ck_parking_facility_coordinates
        CHECK (
            (latitude IS NULL AND longitude IS NULL)
                OR
            (latitude IS NOT NULL AND longitude IS NOT NULL)
            )
);


CREATE TABLE parking_level
(
    id           UUID        NOT NULL,
    facility_id  UUID        NOT NULL,
    code         VARCHAR(30) NOT NULL,
    display_name VARCHAR(100),
    sort_order   INTEGER     NOT NULL,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_parking_level
        PRIMARY KEY (id),

    CONSTRAINT fk_parking_level_facility
        FOREIGN KEY (facility_id)
            REFERENCES parking_facility (id),

    CONSTRAINT uq_parking_level_facility_code
        UNIQUE (facility_id, code),

    CONSTRAINT ck_parking_level_code_not_blank
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT ck_parking_level_display_name_not_blank
        CHECK (display_name IS NULL OR BTRIM(display_name) <> '')
);


CREATE TABLE parking_space
(
    id          UUID        NOT NULL,
    level_id    UUID        NOT NULL,
    code        VARCHAR(30) NOT NULL,
    type        VARCHAR(30) NOT NULL,
    status      VARCHAR(30) NOT NULL,

    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_parking_space
        PRIMARY KEY (id),

    CONSTRAINT fk_parking_space_level
        FOREIGN KEY (level_id)
            REFERENCES parking_level (id),

    CONSTRAINT uq_parking_space_level_code
        UNIQUE (level_id, code),

    CONSTRAINT ck_parking_space_code_not_blank
        CHECK (BTRIM(code) <> ''),

    CONSTRAINT ck_parking_space_type
        CHECK (
            type IN (
                     'STANDARD',
                     'ELECTRIC',
                     'DISABLED',
                     'MOTORCYCLE'
                )
            ),

    CONSTRAINT ck_parking_space_status
        CHECK (
            status IN (
                       'ACTIVE',
                       'OUT_OF_SERVICE'
                )
            )
);


CREATE INDEX idx_parking_level_facility_id
    ON parking_level (facility_id);


CREATE INDEX idx_parking_space_level_id
    ON parking_space (level_id);


CREATE INDEX idx_parking_space_level_status
    ON parking_space (level_id, status);


CREATE INDEX idx_parking_space_level_type
    ON parking_space (level_id, type);