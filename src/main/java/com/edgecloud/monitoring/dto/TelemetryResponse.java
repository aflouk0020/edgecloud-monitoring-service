package com.edgecloud.monitoring.dto;

import com.edgecloud.monitoring.entity.HeartbeatStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TelemetryResponse(
        UUID id,
        String deviceId,
        Double cpuUsage,
        Double memoryUsage,
        Double temperature,
        HeartbeatStatus heartbeatStatus,
        LocalDateTime recordedAt
) {
}