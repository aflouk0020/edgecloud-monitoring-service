package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.TelemetryRequest;
import com.edgecloud.monitoring.dto.TelemetryResponse;
import com.edgecloud.monitoring.entity.HeartbeatStatus;
import com.edgecloud.monitoring.entity.TelemetryMetric;
import com.edgecloud.monitoring.repository.TelemetryMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryMetricRepository repository;

    public TelemetryServiceImpl(TelemetryMetricRepository repository) {
        this.repository = repository;
    }

    @Override
    public TelemetryResponse saveTelemetry(TelemetryRequest request) {
        TelemetryMetric metric = new TelemetryMetric();
        metric.setDeviceId(request.deviceId());
        metric.setCpuUsage(request.cpuUsage());
        metric.setMemoryUsage(request.memoryUsage());
        metric.setTemperature(request.temperature());
        metric.setHeartbeatStatus(HeartbeatStatus.ONLINE);

        TelemetryMetric saved = repository.save(metric);

        return toResponse(saved);
    }

    @Override
    public List<TelemetryResponse> getTelemetryByDeviceId(String deviceId) {
        return repository.findByDeviceIdOrderByRecordedAtDesc(deviceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TelemetryResponse toResponse(TelemetryMetric metric) {
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