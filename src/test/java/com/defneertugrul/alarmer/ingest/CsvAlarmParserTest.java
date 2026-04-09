package com.defneertugrul.alarmer.ingest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvAlarmParserTest {

    @Test
    void parsesCsvFromClasspath() throws Exception {
        CsvAlarmParser parser = new CsvAlarmParser();
        try (InputStream in = getClass().getResourceAsStream("/test-alarms.csv")) {
            List<AlarmDTO> dtos = parser.parse(in);
            assertThat(dtos).hasSize(10);
            AlarmDTO first = dtos.get(0);
            assertThat(first.sensorId()).isEqualTo("ABS-temperature-001");
            assertThat(first.plantSection()).isEqualTo("ABSORBER");
            assertThat(first.metric()).isEqualTo("temperature_C");
            assertThat(first.value()).isEqualTo(135.2);
        }
    }
}
