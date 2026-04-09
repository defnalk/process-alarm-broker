package com.defneertugrul.alarmer.suppression;

import static org.assertj.core.api.Assertions.assertThat;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AlarmSuppressorTest {

    private static Alarm alarm(String sensor, AlarmSeverity sev, Instant ts) {
        return new Alarm(UUID.randomUUID(), sensor, "ABSORBER", "temperature_C", 130.0,
                sev, "msg", ts, ts, false, null);
    }

    private static SuppressionConfig config(int window, int max) {
        SuppressionConfig c = new SuppressionConfig();
        c.setWindowSeconds(window);
        c.setMaxGroupSize(max);
        return c;
    }

    @Test
    void firstAlarmIsNotSuppressed() {
        AlarmSuppressor s = new AlarmSuppressor(config(30, 10));
        Instant t = Instant.parse("2025-07-15T14:30:00Z");
        assertThat(s.evaluate(alarm("S1", AlarmSeverity.HIGH, t), t)).isFalse();
    }

    @Test
    void secondAlarmInWindowIsSuppressed() {
        AlarmSuppressor s = new AlarmSuppressor(config(30, 10));
        Instant t = Instant.parse("2025-07-15T14:30:00Z");
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t), t);
        Alarm a2 = alarm("S1", AlarmSeverity.HIGH, t.plusSeconds(5));
        assertThat(s.evaluate(a2, t.plusSeconds(5))).isTrue();
        assertThat(a2.isSuppressed()).isTrue();
        assertThat(a2.getSuppressionGroupId()).isNotNull();
    }

    @Test
    void differentSeverityIsNotSuppressed() {
        AlarmSuppressor s = new AlarmSuppressor(config(30, 10));
        Instant t = Instant.parse("2025-07-15T14:30:00Z");
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t), t);
        assertThat(s.evaluate(alarm("S1", AlarmSeverity.CRITICAL, t.plusSeconds(1)), t.plusSeconds(1)))
                .isFalse();
    }

    @Test
    void afterWindowExpiresNewGroupStarts() {
        AlarmSuppressor s = new AlarmSuppressor(config(10, 10));
        Instant t = Instant.parse("2025-07-15T14:30:00Z");
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t), t);
        assertThat(s.evaluate(alarm("S1", AlarmSeverity.HIGH, t.plusSeconds(20)), t.plusSeconds(20)))
                .isFalse();
    }

    @Test
    void groupRollsOverAtMaxSize() {
        AlarmSuppressor s = new AlarmSuppressor(config(60, 3));
        Instant t = Instant.parse("2025-07-15T14:30:00Z");
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t), t);              // size 1, not suppressed
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t.plusSeconds(1)), t.plusSeconds(1)); // size 2, suppressed
        s.evaluate(alarm("S1", AlarmSeverity.HIGH, t.plusSeconds(2)), t.plusSeconds(2)); // size 3, suppressed
        // Next should start a new group, NOT suppressed
        assertThat(s.evaluate(alarm("S1", AlarmSeverity.HIGH, t.plusSeconds(3)), t.plusSeconds(3)))
                .isFalse();
    }
}
