package com.defneertugrul.alarmer.suppression;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * In-memory dedup: an alarm is suppressed if a non-suppressed alarm with the
 * same (sensorId, severity) was seen within the configured window.
 *
 * Production swap-in: back this with Redis (SETEX) so multiple broker
 * instances share suppression state.
 */
@Component
public class AlarmSuppressor {

    private final SuppressionConfig config;
    private final Map<Key, Group> groups = new ConcurrentHashMap<>();

    public AlarmSuppressor(SuppressionConfig config) {
        this.config = config;
    }

    /**
     * Decides whether the given alarm should be suppressed. If yes, mutates
     * the alarm (marks suppressed + sets group id) and returns true.
     */
    public boolean evaluate(Alarm alarm, Instant now) {
        Key key = new Key(alarm.getSensorId(), alarm.getSeverity());
        Duration window = Duration.ofSeconds(config.getWindowSeconds());

        Group group = groups.compute(key, (k, existing) -> {
            if (existing == null || Duration.between(existing.firstSeen, now).compareTo(window) > 0) {
                return new Group(UUID.randomUUID(), now, 1);
            }
            if (existing.size >= config.getMaxGroupSize()) {
                return new Group(UUID.randomUUID(), now, 1);
            }
            return new Group(existing.groupId, existing.firstSeen, existing.size + 1);
        });

        if (group.size == 1) {
            return false;
        }
        alarm.markSuppressed(group.groupId);
        return true;
    }

    private record Key(String sensorId, AlarmSeverity severity) {}
    private record Group(UUID groupId, Instant firstSeen, int size) {}
}
