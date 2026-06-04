package com.edgecloud.monitoring.dto;

import java.util.List;

public record HistoricalMetricsResponse(
        List<ServiceMetricResponse> serviceMetrics,
        List<TelemetryResponse> telemetryMetrics
) {
}