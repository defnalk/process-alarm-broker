package com.defneertugrul.alarmer.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Sealed hierarchy of domain events emitted by the broker.
 * Suppressed alarms are emitted as a separate variant so listeners
 * (e.g. WebSocket broadcaster) can ignore them by pattern matching.
 */
public sealed interface AlarmEvent {

    UUID alarmId();
    Instant occurredAt();

    record AlarmAccepted(UUID alarmId, AlarmSeverity severity, String sensorId, Instant occurredAt)
            implements AlarmEvent {}

    record AlarmSuppressed(UUID alarmId, UUID groupId, String sensorId, Instant occurredAt)
            implements AlarmEvent {}
}
