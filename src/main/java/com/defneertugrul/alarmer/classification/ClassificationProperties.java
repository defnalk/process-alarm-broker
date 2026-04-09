package com.defneertugrul.alarmer.classification;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alarm.classification")
public class ClassificationProperties {

    private List<RuleConfig> rules = List.of();

    public List<RuleConfig> getRules() { return rules; }
    public void setRules(List<RuleConfig> rules) { this.rules = rules; }

    public static class RuleConfig {
        private String sensorPattern;
        private String metric;
        private Map<AlarmSeverity, Double> thresholds;

        public String getSensorPattern() { return sensorPattern; }
        public void setSensorPattern(String sensorPattern) { this.sensorPattern = sensorPattern; }

        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }

        public Map<AlarmSeverity, Double> getThresholds() { return thresholds; }
        public void setThresholds(Map<AlarmSeverity, Double> thresholds) { this.thresholds = thresholds; }

        public ClassificationRule toRule() {
            return new ClassificationRule(sensorPattern, metric, thresholds);
        }
    }
}
