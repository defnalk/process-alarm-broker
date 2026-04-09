package com.defneertugrul.alarmer.ingest;

import com.defneertugrul.alarmer.classification.SeverityClassifier;
import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmEvent;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import com.defneertugrul.alarmer.persistence.AlarmRepository;
import com.defneertugrul.alarmer.suppression.AlarmSuppressor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pipeline: classify -> suppress -> persist -> publish event.
 * Returns the persisted Alarm; ignored (sub-threshold) readings return empty.
 */
@Service
public class AlarmIngestService {

    private final SeverityClassifier classifier;
    private final AlarmSuppressor suppressor;
    private final AlarmRepository repo;
    private final ApplicationEventPublisher events;

    public AlarmIngestService(SeverityClassifier classifier,
                              AlarmSuppressor suppressor,
                              AlarmRepository repo,
                              ApplicationEventPublisher events) {
        this.classifier = classifier;
        this.suppressor = suppressor;
        this.repo = repo;
        this.events = events;
    }

    @Transactional
    public Optional<Alarm> ingest(AlarmDTO dto) {
        Optional<AlarmSeverity> sev = classifier.classify(dto.sensorId(), dto.metric(), dto.value());
        if (sev.isEmpty()) return Optional.empty();

        Instant now = Instant.now();
        Alarm alarm = new Alarm(
                UUID.randomUUID(),
                dto.sensorId(),
                dto.plantSection(),
                dto.metric(),
                dto.value(),
                sev.get(),
                "%s reading %.2f triggered %s".formatted(dto.metric(), dto.value(), sev.get()),
                dto.timestamp(),
                now,
                false,
                null);

        boolean suppressed = suppressor.evaluate(alarm, now);
        Alarm saved = repo.save(alarm);

        if (suppressed) {
            events.publishEvent(new AlarmEvent.AlarmSuppressed(
                    saved.getId(), saved.getSuppressionGroupId(), saved.getSensorId(), now));
        } else {
            events.publishEvent(new AlarmEvent.AlarmAccepted(
                    saved.getId(), saved.getSeverity(), saved.getSensorId(), now));
        }
        return Optional.of(saved);
    }

    @Transactional
    public List<Alarm> ingestAll(List<AlarmDTO> dtos) {
        List<Alarm> result = new ArrayList<>();
        for (AlarmDTO dto : dtos) {
            ingest(dto).ifPresent(result::add);
        }
        return result;
    }
}
