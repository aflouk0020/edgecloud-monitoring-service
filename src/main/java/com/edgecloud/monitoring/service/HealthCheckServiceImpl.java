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

import java.util.List;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final ServiceMetricRepository serviceMetricRepository;
    private final RestTemplate restTemplate;

    public HealthCheckServiceImpl(
            MonitoredServiceRepository monitoredServiceRepository,
            ServiceMetricRepository serviceMetricRepository,
            RestTemplate restTemplate) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.serviceMetricRepository = serviceMetricRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<MonitoredServiceResponse> checkAllServices() {
        List<MonitoredService> services = monitoredServiceRepository.findAll();

        return services.stream()
                .map(this::checkService)
                .toList();
    }

    private MonitoredServiceResponse checkService(MonitoredService service) {
        long startTime = System.currentTimeMillis();

        ServiceMetric metric = new ServiceMetric();
        metric.setServiceId(service.getId());

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    service.getServiceUrl() + "/actuator/health",
                    String.class
            );

            long responseTime = System.currentTimeMillis() - startTime;

            metric.setResponseTimeMs(responseTime);
            metric.setStatusCode(response.getStatusCode().value());
            metric.setUptimeStatus(UptimeStatus.UP);

            service.setStatus("UP");

        } catch (Exception ex) {
            long responseTime = System.currentTimeMillis() - startTime;

            metric.setResponseTimeMs(responseTime);
            metric.setStatusCode(0);
            metric.setUptimeStatus(UptimeStatus.DOWN);

            service.setStatus("DOWN");
        }

        serviceMetricRepository.save(metric);
        monitoredServiceRepository.save(service);

        return new MonitoredServiceResponse(
                service.getId(),
                service.getServiceName(),
                service.getServiceUrl(),
                service.getStatus(),
                service.getCreatedAt()
        );
    }
}