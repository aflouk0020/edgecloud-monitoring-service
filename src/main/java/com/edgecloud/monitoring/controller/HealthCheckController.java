package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.MonitoredServiceResponse;
import com.edgecloud.monitoring.dto.ServiceMetricResponse;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import com.edgecloud.monitoring.service.HealthCheckService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health-checks")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;
    private final ServiceMetricRepository serviceMetricRepository;

    public HealthCheckController(
            HealthCheckService healthCheckService,
            ServiceMetricRepository serviceMetricRepository) {
        this.healthCheckService = healthCheckService;
        this.serviceMetricRepository = serviceMetricRepository;
    }

    @PostMapping
    public List<MonitoredServiceResponse> checkAllServices() {
        return healthCheckService.checkAllServices();
    }

    @GetMapping("/metrics")
    public List<ServiceMetricResponse> getAllMetrics() {
        return serviceMetricRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ServiceMetricResponse toResponse(ServiceMetric metric) {
        return new ServiceMetricResponse(
                metric.getId(),
                metric.getServiceId(),
                metric.getResponseTimeMs(),
                metric.getStatusCode(),
                metric.getUptimeStatus(),
                metric.getRecordedAt()
        );
    }
}