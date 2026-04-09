package com.defneertugrul.alarmer.persistence;

import com.defneertugrul.alarmer.api.StatsResponse;
import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlarmQueryService {

    private final AlarmRepository repo;

    public AlarmQueryService(AlarmRepository repo) {
        this.repo = repo;
    }

    public Page<Alarm> search(Instant from, Instant to, AlarmSeverity severity, String section,
                              boolean suppressed, int page, int size) {
        return repo.search(from, to, severity, section, suppressed, PageRequest.of(page, size));
    }

    public Optional<Alarm> findById(UUID id) {
        return repo.findById(id);
    }

    public StatsResponse stats() {
        Map<AlarmSeverity, Long> bySeverity = new EnumMap<>(AlarmSeverity.class);
        for (AlarmSeverity s : AlarmSeverity.values()) bySeverity.put(s, 0L);
        repo.countBySeverity().forEach(c -> bySeverity.put(c.getSeverity(), c.getCount()));

        Map<String, Long> top = new LinkedHashMap<>();
        repo.topSensors(PageRequest.of(0, 5)).forEach(s -> top.put(s.getSensorId(), s.getCount()));

        Instant lastHour = Instant.now().minus(Duration.ofHours(1));
        long lastHourCount = repo.countByRawTimestampGreaterThanEqualAndSuppressedFalse(lastHour);
        double perMinute = lastHourCount / 60.0;

        return new StatsResponse(bySeverity, top, perMinute);
    }
}
