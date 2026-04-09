package com.defneertugrul.alarmer.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@Import(AlarmQueryService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("itest")
class AlarmRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("alarmbroker")
            .withUsername("test")
            .withPassword("test");

    static {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
    }

    @Autowired AlarmRepository repo;
    @Autowired AlarmQueryService service;

    @Test
    void persistsAndQueries() {
        Instant now = Instant.now();
        Alarm a = new Alarm(UUID.randomUUID(), "ABS-T-001", "ABSORBER", "temperature_C", 135.0,
                AlarmSeverity.HIGH, "msg", now, now, false, null);
        repo.save(a);

        var page = service.search(null, null, AlarmSeverity.HIGH, "ABSORBER", false, 0, 10);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getSensorId()).isEqualTo("ABS-T-001");
    }

    @Test
    void statsAggregates() {
        Instant now = Instant.now();
        repo.save(new Alarm(UUID.randomUUID(), "S1", "ABSORBER", "temperature_C", 130.0,
                AlarmSeverity.HIGH, "m", now, now, false, null));
        repo.save(new Alarm(UUID.randomUUID(), "S1", "ABSORBER", "temperature_C", 145.0,
                AlarmSeverity.CRITICAL, "m", now, now, false, null));
        var stats = service.stats();
        assertThat(stats.countBySeverity().get(AlarmSeverity.HIGH)).isGreaterThanOrEqualTo(1);
        assertThat(stats.countBySeverity().get(AlarmSeverity.CRITICAL)).isGreaterThanOrEqualTo(1);
        assertThat(stats.topSensors()).containsKey("S1");
    }
}
