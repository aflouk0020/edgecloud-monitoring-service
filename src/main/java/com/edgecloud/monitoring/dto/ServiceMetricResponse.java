package com.edgecloud.monitoring.dto;

import com.edgecloud.monitoring.entity.UptimeStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceMetricResponse(
        UUID id,
        UUID serviceId,
        Long responseTimeMs,
        Integer statusCode,
        UptimeStatus uptimeStatus,
        LocalDateTime recordedAt
) {
}