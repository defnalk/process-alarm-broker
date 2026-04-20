package com.defneertugrul.alarmer.ingest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvAlarmParser {

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();

    public List<AlarmDTO> parse(InputStream input) throws IOException {
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             CSVParser parser = CSVParser.parse(reader, FORMAT)) {
            List<AlarmDTO> result = new ArrayList<>();
            for (CSVRecord record : parser) {
                try {
                    result.add(new AlarmDTO(
                            record.get("sensor_id"),
                            record.get("plant_section"),
                            record.get("metric"),
                            Double.parseDouble(record.get("value")),
                            Instant.parse(record.get("timestamp"))));
                } catch (IllegalArgumentException | java.time.format.DateTimeParseException ex) {
                    // Commons-CSV's NumberFormatException / header-missing
                    // IllegalArgumentException and Instant's parse failures all
                    // bubble up without row context. Rewrap so the caller can
                    // tell the operator which CSV line was bad.
                    throw new IllegalArgumentException(
                            "Invalid CSV row at line " + record.getRecordNumber() + ": " + ex.getMessage(), ex);
                }
            }
            return result;
        }
    }
}
