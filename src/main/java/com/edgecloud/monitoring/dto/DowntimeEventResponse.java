package com.edgecloud.monitoring.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DowntimeEventResponse(
        UUID id,
        UUID serviceId,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Long durationSeconds,
        boolean active
) {
}
