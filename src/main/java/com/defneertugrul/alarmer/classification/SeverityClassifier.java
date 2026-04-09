package com.defneertugrul.alarmer.classification;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.Optional;

public interface SeverityClassifier {
    /**
     * Returns the severity for a sensor reading, or empty if no rule matches
     * or the value is below the lowest configured threshold.
     */
    Optional<AlarmSeverity> classify(String sensorId, String metric, double value);
}
