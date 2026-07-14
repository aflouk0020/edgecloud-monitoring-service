package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoringAnalyticsResponse;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.entity.TelemetryMetric;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import com.edgecloud.monitoring.repository.TelemetryMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MonitoringAnalyticsServiceImpl implements MonitoringAnalyticsService {

    private final ServiceMetricRepository serviceMetricRepository;
    private final TelemetryMetricRepository telemetryMetricRepository;

    public MonitoringAnalyticsServiceImpl(
            ServiceMetricRepository serviceMetricRepository,
            TelemetryMetricRepository telemetryMetricRepository) {
        this.serviceMetricRepository = serviceMetricRepository;
        this.telemetryMetricRepository = telemetryMetricRepository;
    }

    @Override
    public MonitoringAnalyticsResponse getAnalytics() {
        List<ServiceMetric> serviceMetrics = serviceMetricRepository.findAll();
        List<TelemetryMetric> telemetryMetrics = telemetryMetricRepository.findAll();

        long upChecks = serviceMetrics.stream()
                .filter(metric -> metric.getUptimeStatus() == UptimeStatus.UP)
                .count();

        long downChecks = serviceMetrics.stream()
                .filter(metric -> metric.getUptimeStatus() == UptimeStatus.DOWN)
                .count();

        return new MonitoringAnalyticsResponse(
                serviceMetrics.size(),
                upChecks,
                downChecks,
                percentage(upChecks, serviceMetrics.size()),
                averageResponseTime(serviceMetrics),
                telemetryMetrics.size(),
                averageTelemetry(telemetryMetrics, TelemetryMetric::getCpuUsage),
                averageTelemetry(telemetryMetrics, TelemetryMetric::getMemoryUsage),
                averageTelemetry(telemetryMetrics, TelemetryMetric::getTemperature)
        );
    }

    private double averageResponseTime(List<ServiceMetric> metrics) {
        return round(metrics.stream()
                .map(ServiceMetric::getResponseTimeMs)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0));
    }

    private double averageTelemetry(
            List<TelemetryMetric> metrics,
            java.util.function.Function<TelemetryMetric, Double> valueExtractor) {
        return round(metrics.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0));
    }

    private double percentage(long value, long total) {
        if (total == 0) {
            return 0.0;
        }

        return round((value * 100.0) / total);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
