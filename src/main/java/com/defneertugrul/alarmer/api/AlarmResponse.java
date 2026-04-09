package com.defneertugrul.alarmer.api;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Instant;
import java.util.UUID;

public record AlarmResponse(
        UUID id,
        String sensorId,
        String plantSection,
        String metric,
        Double value,
        AlarmSeverity severity,
        String message,
        Instant rawTimestamp,
        Instant ingestedAt,
        boolean suppressed,
        UUID suppressionGroupId
) {
    public static AlarmResponse from(Alarm a) {
        return new AlarmResponse(
                a.getId(), a.getSensorId(), a.getPlantSection(), a.getMetric(), a.getValue(),
                a.getSeverity(), a.getMessage(), a.getRawTimestamp(), a.getIngestedAt(),
                a.isSuppressed(), a.getSuppressionGroupId());
    }
}
