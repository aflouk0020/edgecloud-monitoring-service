package com.edgecloud.monitoring.repository;

import com.edgecloud.monitoring.entity.ServiceMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceMetricRepository extends JpaRepository<ServiceMetric, UUID> {

    List<ServiceMetric> findByServiceIdOrderByRecordedAtDesc(UUID serviceId);
}