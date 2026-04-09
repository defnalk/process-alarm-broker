package com.defneertugrul.alarmer.persistence;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AlarmRepository extends JpaRepository<Alarm, UUID>, JpaSpecificationExecutor<Alarm> {

    @Query("SELECT a.severity AS severity, COUNT(a) AS count FROM Alarm a WHERE a.suppressed = false GROUP BY a.severity")
    List<SeverityCount> countBySeverity();

    @Query("""
            SELECT a.sensorId AS sensorId, COUNT(a) AS count FROM Alarm a
            WHERE a.suppressed = false
            GROUP BY a.sensorId ORDER BY COUNT(a) DESC
            """)
    List<SensorCount> topSensors(Pageable pageable);

    long countByRawTimestampGreaterThanEqualAndSuppressedFalse(Instant from);

    interface SeverityCount {
        AlarmSeverity getSeverity();
        long getCount();
    }

    interface SensorCount {
        String getSensorId();
        long getCount();
    }
}
