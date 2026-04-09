package com.defneertugrul.alarmer.classification;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.Map;

public record ClassificationRule(String sensorPattern, String metric, Map<AlarmSeverity, Double> thresholds) {
}
