package com.defneertugrul.alarmer.ingest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.defneertugrul.alarmer.config.JacksonConfig;
import com.defneertugrul.alarmer.domain.Alarm;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlarmIngestController.class)
@Import(JacksonConfig.class)
class AlarmIngestControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AlarmIngestService service;
    @MockBean CsvAlarmParser parser;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Test
    void ingestSingle() throws Exception {
        Instant now = Instant.parse("2025-07-15T14:30:00Z");
        AlarmDTO dto = new AlarmDTO("ABS-T-001", "ABSORBER", "temperature_C", 135.0, now);
        Alarm saved = new Alarm(UUID.randomUUID(), "ABS-T-001", "ABSORBER", "temperature_C", 135.0,
                AlarmSeverity.HIGH, "msg", now, now, false, null);
        when(service.ingest(any())).thenReturn(Optional.of(saved));

        mvc.perform(post("/api/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void subThresholdReturnsNoContent() throws Exception {
        when(service.ingest(any())).thenReturn(Optional.empty());
        AlarmDTO dto = new AlarmDTO("ABS-T-001", "ABSORBER", "temperature_C", 50.0,
                Instant.parse("2025-07-15T14:30:00Z"));
        mvc.perform(post("/api/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }
}
