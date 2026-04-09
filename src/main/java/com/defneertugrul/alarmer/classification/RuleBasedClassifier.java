package com.defneertugrul.alarmer.classification;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
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
                        // Sort thresholds descending so classify() can short-circuit
                        // on the first match instead of streaming every entry.
                        r.getThresholds().entrySet().stream()
                                .sorted(Map.Entry.<AlarmSeverity, Double>comparingByValue().reversed())
                                .toList()))
                .toList();
    }

    @Override
    public Optional<AlarmSeverity> classify(String sensorId, String metric, double value) {
        for (CompiledRule rule : compiled) {
            if (!rule.metric.equals(metric)) continue;
            if (!rule.pattern.matcher(sensorId).matches()) continue;
            for (Map.Entry<AlarmSeverity, Double> e : rule.sortedThresholds) {
                if (value >= e.getValue()) {
                    return Optional.of(e.getKey());
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private record CompiledRule(Pattern pattern, String metric,
                                List<Map.Entry<AlarmSeverity, Double>> sortedThresholds) {}
}
