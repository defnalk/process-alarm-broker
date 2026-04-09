package com.defneertugrul.alarmer.ingest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AlarmDTO(
        @NotBlank String sensorId,
        String plantSection,
        @NotBlank String metric,
        @NotNull Double value,
        @NotNull Instant timestamp
) {
}
