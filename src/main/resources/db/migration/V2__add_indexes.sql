CREATE INDEX idx_alarms_sensor_severity_time
    ON alarms (sensor_id, severity, raw_timestamp);

CREATE INDEX idx_alarms_plant_section
    ON alarms (plant_section);

CREATE INDEX idx_alarms_raw_timestamp
    ON alarms (raw_timestamp);
