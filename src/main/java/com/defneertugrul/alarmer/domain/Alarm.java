package com.defneertugrul.alarmer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alarms")
public class Alarm {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "plant_section")
    private String plantSection;

    @Column(name = "metric")
    private String metric;

    @Column(name = "reading_value")
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private AlarmSeverity severity;

    @Column(name = "message")
    private String message;

    @Column(name = "raw_timestamp", nullable = false)
    private Instant rawTimestamp;

    @Column(name = "ingested_at", nullable = false)
    private Instant ingestedAt;

    @Column(name = "suppressed", nullable = false)
    private boolean suppressed;

    @Column(name = "suppression_group_id")
    private UUID suppressionGroupId;

    protected Alarm() {
    }

    public Alarm(UUID id, String sensorId, String plantSection, String metric, Double value,
                 AlarmSeverity severity, String message, Instant rawTimestamp, Instant ingestedAt,
                 boolean suppressed, UUID suppressionGroupId) {
        this.id = id;
        this.sensorId = sensorId;
        this.plantSection = plantSection;
        this.metric = metric;
        this.value = value;
        this.severity = severity;
        this.message = message;
        this.rawTimestamp = rawTimestamp;
        this.ingestedAt = ingestedAt;
        this.suppressed = suppressed;
        this.suppressionGroupId = suppressionGroupId;
    }

    public UUID getId() { return id; }
    public String getSensorId() { return sensorId; }
    public String getPlantSection() { return plantSection; }
    public String getMetric() { return metric; }
    public Double getValue() { return value; }
    public AlarmSeverity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public Instant getRawTimestamp() { return rawTimestamp; }
    public Instant getIngestedAt() { return ingestedAt; }
    public boolean isSuppressed() { return suppressed; }
    public UUID getSuppressionGroupId() { return suppressionGroupId; }

    public void markSuppressed(UUID groupId) {
        this.suppressed = true;
        this.suppressionGroupId = groupId;
    }
}
