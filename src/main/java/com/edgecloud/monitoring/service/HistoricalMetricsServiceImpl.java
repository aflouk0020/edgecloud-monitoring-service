package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.HistoricalMetricsResponse;
import com.edgecloud.monitoring.dto.ServiceMetricResponse;
import com.edgecloud.monitoring.dto.TelemetryResponse;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.entity.TelemetryMetric;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import com.edgecloud.monitoring.repository.TelemetryMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalMetricsServiceImpl implements HistoricalMetricsService {

    private final ServiceMetricRepository serviceMetricRepository;
    private final TelemetryMetricRepository telemetryMetricRepository;

    public HistoricalMetricsServiceImpl(
            ServiceMetricRepository serviceMetricRepository,
            TelemetryMetricRepository telemetryMetricRepository) {
        this.serviceMetricRepository = serviceMetricRepository;
        this.telemetryMetricRepository = telemetryMetricRepository;
    }

    @Override
    public HistoricalMetricsResponse getHistoricalMetrics() {
        List<ServiceMetricResponse> serviceMetrics = serviceMetricRepository
                .findAllByOrderByRecordedAtDesc()
                .stream()
                .map(this::toServiceMetricResponse)
                .toList();

        List<TelemetryResponse> telemetryMetrics = telemetryMetricRepository
                .findAllByOrderByRecordedAtDesc()
                .stream()
                .map(this::toTelemetryResponse)
                .toList();

        return new HistoricalMetricsResponse(serviceMetrics, telemetryMetrics);
    }

    private ServiceMetricResponse toServiceMetricResponse(ServiceMetric metric) {
        return new ServiceMetricResponse(
                metric.getId(),
                metric.getServiceId(),
                metric.getResponseTimeMs(),
                metric.getStatusCode(),
                metric.getUptimeStatus(),
                metric.getRecordedAt()
        );
    }

    private TelemetryResponse toTelemetryResponse(TelemetryMetric metric) {
        return new TelemetryResponse(
                metric.getId(),
                metric.getDeviceId(),
                metric.getCpuUsage(),
                metric.getMemoryUsage(),
                metric.getTemperature(),
                metric.getHeartbeatStatus(),
                metric.getRecordedAt()
        );
    }
}