package com.defneertugrul.alarmer.api;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.Map;

public record StatsResponse(
        Map<AlarmSeverity, Long> countBySeverity,
        Map<String, Long> topSensors,
        double alarmsPerMinuteLastHour
) {
}
