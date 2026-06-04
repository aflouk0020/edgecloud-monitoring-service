package com.edgecloud.monitoring.repository;

import com.edgecloud.monitoring.entity.TelemetryMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TelemetryMetricRepository extends JpaRepository<TelemetryMetric, UUID> {

    List<TelemetryMetric> findByDeviceIdOrderByRecordedAtDesc(String deviceId);
    List<TelemetryMetric> findAllByOrderByRecordedAtDesc();
}