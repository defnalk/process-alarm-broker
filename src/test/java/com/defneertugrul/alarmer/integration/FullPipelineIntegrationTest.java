package com.defneertugrul.alarmer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.defneertugrul.alarmer.persistence.AlarmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FullPipelineIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired AlarmRepository repo;

    @Test
    void csvUploadFlowsThroughPipeline() throws Exception {
        repo.deleteAll();
        byte[] csv = getClass().getResourceAsStream("/test-alarms.csv").readAllBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test-alarms.csv", "text/csv", csv);

        mvc.perform(multipart("/api/alarms/csv").file(file))
                .andExpect(status().isCreated());

        // 10 rows in CSV: temperature_C 72.0 (sub-threshold), 90.0 (LOW),
        // 135.2/136.0/142.0 (HIGH/HIGH/CRITICAL), pressure 520/610 (HIGH/CRITICAL),
        // pressure 210 (LOW), co2_loading 0.46/0.51 (HIGH/CRITICAL).
        // 9 above threshold; sub-threshold (72.0) is dropped.
        assertThat(repo.count()).isEqualTo(9);

        mvc.perform(get("/api/alarms/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_by_severity").exists())
                .andExpect(jsonPath("$.top_sensors").exists());
    }
}
