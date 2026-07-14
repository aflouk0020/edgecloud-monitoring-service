package com.edgecloud.monitoring.dto;

public record MonitoringAnalyticsResponse(
        long totalServiceChecks,
        long upServiceChecks,
        long downServiceChecks,
        double serviceUptimePercentage,
        double averageResponseTimeMs,
        long telemetrySamples,
        double averageCpuUsage,
        double averageMemoryUsage,
        double averageTemperature
) {
}
