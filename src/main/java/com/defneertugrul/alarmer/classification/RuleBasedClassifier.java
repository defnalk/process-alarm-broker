package com.defneertugrul.alarmer.classification;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedClassifier implements SeverityClassifier {

    private final List<CompiledRule> compiled;

    public RuleBasedClassifier(ClassificationProperties props) {
        this.compiled = props.getRules().stream()
                .map(r -> new CompiledRule(
                        Pattern.compile(r.getSensorPattern()),
                        r.getMetric(),
                        r.getThresholds()))
                .toList();
    }

    @Override
    public Optional<AlarmSeverity> classify(String sensorId, String metric, double value) {
        for (CompiledRule rule : compiled) {
            if (!rule.metric.equals(metric)) continue;
            if (!rule.pattern.matcher(sensorId).matches()) continue;
            return highestExceeded(rule.thresholds, value);
        }
        return Optional.empty();
    }

    private static Optional<AlarmSeverity> highestExceeded(Map<AlarmSeverity, Double> thresholds, double value) {
        return thresholds.entrySet().stream()
                .filter(e -> value >= e.getValue())
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    private record CompiledRule(Pattern pattern, String metric, Map<AlarmSeverity, Double> thresholds) {}
}
