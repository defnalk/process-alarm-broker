package com.defneertugrul.alarmer.classification;

import static org.assertj.core.api.Assertions.assertThat;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RuleBasedClassifierTest {

    private static RuleBasedClassifier classifier() {
        ClassificationProperties props = new ClassificationProperties();
        ClassificationProperties.RuleConfig temp = new ClassificationProperties.RuleConfig();
        temp.setSensorPattern(".*temperature.*");
        temp.setMetric("temperature_C");
        temp.setThresholds(Map.of(
                AlarmSeverity.LOW, 80.0,
                AlarmSeverity.MEDIUM, 100.0,
                AlarmSeverity.HIGH, 120.0,
                AlarmSeverity.CRITICAL, 140.0));
        ClassificationProperties.RuleConfig pres = new ClassificationProperties.RuleConfig();
        pres.setSensorPattern(".*pressure.*");
        pres.setMetric("pressure_kPa");
        pres.setThresholds(Map.of(
                AlarmSeverity.LOW, 200.0,
                AlarmSeverity.MEDIUM, 350.0,
                AlarmSeverity.HIGH, 500.0,
                AlarmSeverity.CRITICAL, 600.0));
        props.setRules(List.of(temp, pres));
        return new RuleBasedClassifier(props);
    }

    static List<Arguments> cases() {
        return List.of(
                Arguments.of("ABS-temperature-001", "temperature_C", 50.0, Optional.empty()),
                Arguments.of("ABS-temperature-001", "temperature_C", 85.0, Optional.of(AlarmSeverity.LOW)),
                Arguments.of("ABS-temperature-001", "temperature_C", 105.0, Optional.of(AlarmSeverity.MEDIUM)),
                Arguments.of("ABS-temperature-001", "temperature_C", 125.0, Optional.of(AlarmSeverity.HIGH)),
                Arguments.of("ABS-temperature-001", "temperature_C", 200.0, Optional.of(AlarmSeverity.CRITICAL)),
                Arguments.of("STR-pressure-003", "pressure_kPa", 600.0, Optional.of(AlarmSeverity.CRITICAL)),
                Arguments.of("STR-pressure-003", "pressure_kPa", 199.9, Optional.empty()),
                Arguments.of("ABS-temperature-001", "flow_m3h", 1000.0, Optional.empty()),
                Arguments.of("unknown-sensor", "temperature_C", 200.0, Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource("cases")
    void classifies(String sensorId, String metric, double value, Optional<AlarmSeverity> expected) {
        assertThat(classifier().classify(sensorId, metric, value)).isEqualTo(expected);
    }
}
