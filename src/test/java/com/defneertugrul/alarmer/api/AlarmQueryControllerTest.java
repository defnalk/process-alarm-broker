package com.defneertugrul.alarmer.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.defneertugrul.alarmer.config.JacksonConfig;
import com.defneertugrul.alarmer.domain.AlarmSeverity;
import com.defneertugrul.alarmer.persistence.AlarmQueryService;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlarmQueryController.class)
@Import(JacksonConfig.class)
class AlarmQueryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AlarmQueryService service;

    @Test
    void listReturnsEmpty() throws Exception {
        Page<com.defneertugrul.alarmer.domain.Alarm> empty = new PageImpl<>(java.util.List.of());
        when(service.search(any(), any(), any(), any(), anyBoolean(), anyInt(), anyInt())).thenReturn(empty);
        mvc.perform(get("/api/alarms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void statsReturnsAggregates() throws Exception {
        Map<AlarmSeverity, Long> bySev = new EnumMap<>(AlarmSeverity.class);
        bySev.put(AlarmSeverity.HIGH, 3L);
        Map<String, Long> top = new LinkedHashMap<>();
        top.put("S1", 2L);
        when(service.stats()).thenReturn(new StatsResponse(bySev, top, 1.5));

        mvc.perform(get("/api/alarms/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_by_severity.HIGH").value(3))
                .andExpect(jsonPath("$.alarms_per_minute_last_hour").value(1.5));
    }
}
