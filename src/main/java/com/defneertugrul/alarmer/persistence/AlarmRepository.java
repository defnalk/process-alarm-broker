package com.defneertugrul.alarmer.persistence;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {

    @Query("""
            SELECT a FROM Alarm a
            WHERE (:from IS NULL OR a.rawTimestamp >= :from)
              AND (:to IS NULL OR a.rawTimestamp < :to)
              AND (:severity IS NULL OR a.severity = :severity)
              AND (:section IS NULL OR a.plantSection = :section)
              AND a.suppressed = :suppressed
            ORDER BY a.rawTimestamp DESC
            """)
    Page<Alarm> search(@Param("from") Instant from,
                       @Param("to") Instant to,
                       @Param("severity") AlarmSeverity severity,
                       @Param("section") String section,
                       @Param("suppressed") boolean suppressed,
                       Pageable pageable);

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
