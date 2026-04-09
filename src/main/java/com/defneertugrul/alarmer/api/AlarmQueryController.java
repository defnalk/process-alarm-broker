package com.defneertugrul.alarmer.api;

import com.defneertugrul.alarmer.domain.AlarmSeverity;
import com.defneertugrul.alarmer.persistence.AlarmQueryService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alarms")
public class AlarmQueryController {

    private final AlarmQueryService service;

    public AlarmQueryController(AlarmQueryService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlarmResponse> list(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) AlarmSeverity severity,
            @RequestParam(required = false) String section,
            @RequestParam(required = false, defaultValue = "false") boolean suppressed,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size) {
        return service.search(from, to, severity, section, suppressed, page, size)
                .map(AlarmResponse::from)
                .getContent();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlarmResponse> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(AlarmResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return service.stats();
    }
}
