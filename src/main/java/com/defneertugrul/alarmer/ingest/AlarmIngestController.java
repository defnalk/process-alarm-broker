package com.defneertugrul.alarmer.ingest;

import com.defneertugrul.alarmer.api.AlarmResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/alarms")
public class AlarmIngestController {

    private final AlarmIngestService service;
    private final CsvAlarmParser parser;

    public AlarmIngestController(AlarmIngestService service, CsvAlarmParser parser) {
        this.service = service;
        this.parser = parser;
    }

    @PostMapping
    public ResponseEntity<AlarmResponse> ingest(@Valid @RequestBody AlarmDTO dto) {
        return service.ingest(dto)
                .map(AlarmResponse::from)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(value = "/csv", consumes = "multipart/form-data")
    public ResponseEntity<List<AlarmResponse>> ingestCsv(@RequestParam("file") MultipartFile file) throws IOException {
        List<AlarmDTO> dtos = parser.parse(file.getInputStream());
        List<AlarmResponse> persisted = service.ingestAll(dtos).stream()
                .map(AlarmResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(persisted);
    }
}
