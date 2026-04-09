CREATE TABLE alarms (
    id                    UUID PRIMARY KEY,
    sensor_id             VARCHAR(128) NOT NULL,
    plant_section         VARCHAR(64),
    metric                VARCHAR(64),
    reading_value         DOUBLE PRECISION,
    severity              VARCHAR(16) NOT NULL,
    message               VARCHAR(512),
    raw_timestamp         TIMESTAMP WITH TIME ZONE NOT NULL,
    ingested_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    suppressed            BOOLEAN NOT NULL DEFAULT FALSE,
    suppression_group_id  UUID
);
