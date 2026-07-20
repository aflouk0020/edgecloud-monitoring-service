package com.edgecloud.monitoring.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceAvailabilityResponse(
        UUID serviceId,
        String serviceName,
        String currentStatus,
        long totalChecks,
        long upChecks,
        long downChecks,
        double uptimePercentage,
        double averageResponseTimeMs,
        long downtimeEventCount,
        long totalDowntimeSeconds,
        LocalDateTime lastDowntimeStartedAt,
        LocalDateTime lastRecoveredAt,
        boolean currentlyDown
) {
}
