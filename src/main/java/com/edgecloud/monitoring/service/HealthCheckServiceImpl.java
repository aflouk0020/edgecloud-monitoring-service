package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoredServiceResponse;
import com.edgecloud.monitoring.entity.MonitoredService;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.repository.MonitoredServiceRepository;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final ServiceMetricRepository serviceMetricRepository;
    private final DowntimeTrackingService downtimeTrackingService;
    private final RestTemplate restTemplate;

    public HealthCheckServiceImpl(
            MonitoredServiceRepository monitoredServiceRepository,
            ServiceMetricRepository serviceMetricRepository,
            DowntimeTrackingService downtimeTrackingService,
            RestTemplate restTemplate) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.serviceMetricRepository = serviceMetricRepository;
        this.downtimeTrackingService = downtimeTrackingService;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<MonitoredServiceResponse> checkAllServices() {
        return monitoredServiceRepository.findAll()
                .stream()
                .map(this::checkService)
                .toList();
    }

    private MonitoredServiceResponse checkService(MonitoredService service) {
        String previousStatus = service.getStatus();
        long startTime = System.currentTimeMillis();

        ServiceMetric metric = new ServiceMetric();
        metric.setServiceId(service.getId());

        UptimeStatus currentStatus;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    service.getServiceUrl() + "/actuator/health",
                    String.class
            );

            metric.setResponseTimeMs(System.currentTimeMillis() - startTime);
            metric.setStatusCode(response.getStatusCode().value());
            currentStatus = UptimeStatus.UP;

        } catch (Exception ex) {
            metric.setResponseTimeMs(System.currentTimeMillis() - startTime);
            metric.setStatusCode(0);
            currentStatus = UptimeStatus.DOWN;
        }

        LocalDateTime checkedAt = LocalDateTime.now();

        metric.setUptimeStatus(currentStatus);
        service.setStatus(currentStatus.name());

        serviceMetricRepository.save(metric);
        monitoredServiceRepository.save(service);

        downtimeTrackingService.recordStatusTransition(
                service.getId(),
                previousStatus,
                currentStatus,
                checkedAt
        );

        return new MonitoredServiceResponse(
                service.getId(),
                service.getServiceName(),
                service.getServiceUrl(),
                service.getStatus(),
                service.getCreatedAt()
        );
    }
}
