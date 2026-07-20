package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.DowntimeEventResponse;
import com.edgecloud.monitoring.dto.ServiceAvailabilityResponse;
import com.edgecloud.monitoring.entity.DowntimeEvent;
import com.edgecloud.monitoring.entity.MonitoredService;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.exception.ServiceNotFoundException;
import com.edgecloud.monitoring.repository.DowntimeEventRepository;
import com.edgecloud.monitoring.repository.MonitoredServiceRepository;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ServiceAvailabilityServiceImpl
        implements ServiceAvailabilityService {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final ServiceMetricRepository serviceMetricRepository;
    private final DowntimeEventRepository downtimeEventRepository;

    public ServiceAvailabilityServiceImpl(
            MonitoredServiceRepository monitoredServiceRepository,
            ServiceMetricRepository serviceMetricRepository,
            DowntimeEventRepository downtimeEventRepository) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.serviceMetricRepository = serviceMetricRepository;
        this.downtimeEventRepository = downtimeEventRepository;
    }

    @Override
    public ServiceAvailabilityResponse getAvailability(UUID serviceId) {
        MonitoredService service = findService(serviceId);

        List<ServiceMetric> metrics = serviceMetricRepository
                .findByServiceIdOrderByRecordedAtDesc(serviceId);

        List<DowntimeEvent> downtimeEvents = downtimeEventRepository
                .findByServiceIdOrderByStartedAtDesc(serviceId);

        long upChecks = metrics.stream()
                .filter(metric -> metric.getUptimeStatus() == UptimeStatus.UP)
                .count();

        long downChecks = metrics.stream()
                .filter(metric -> metric.getUptimeStatus() == UptimeStatus.DOWN)
                .count();

        LocalDateTime now = LocalDateTime.now();

        long totalDowntimeSeconds = downtimeEvents.stream()
                .mapToLong(event -> downtimeDurationSeconds(event, now))
                .sum();

        LocalDateTime lastDowntimeStartedAt = downtimeEvents.stream()
                .map(DowntimeEvent::getStartedAt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        LocalDateTime lastRecoveredAt = downtimeEvents.stream()
                .map(DowntimeEvent::getEndedAt)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        boolean activeDowntimeExists = downtimeEvents.stream()
                .anyMatch(event -> event.getEndedAt() == null);

        return new ServiceAvailabilityResponse(
                service.getId(),
                service.getServiceName(),
                service.getStatus(),
                metrics.size(),
                upChecks,
                downChecks,
                percentage(upChecks, metrics.size()),
                averageResponseTime(metrics),
                downtimeEvents.size(),
                totalDowntimeSeconds,
                lastDowntimeStartedAt,
                lastRecoveredAt,
                activeDowntimeExists
                        || "DOWN".equalsIgnoreCase(service.getStatus())
        );
    }

    @Override
    public List<DowntimeEventResponse> getDowntimeHistory(UUID serviceId) {
        findService(serviceId);

        return downtimeEventRepository
                .findByServiceIdOrderByStartedAtDesc(serviceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MonitoredService findService(UUID serviceId) {
        return monitoredServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));
    }

    private long downtimeDurationSeconds(
            DowntimeEvent event,
            LocalDateTime now) {

        if (event.getDurationSeconds() != null) {
            return Math.max(0, event.getDurationSeconds());
        }

        if (event.getStartedAt() == null) {
            return 0;
        }

        return Math.max(
                0,
                Duration.between(event.getStartedAt(), now).getSeconds()
        );
    }

    private double averageResponseTime(List<ServiceMetric> metrics) {
        double average = metrics.stream()
                .map(ServiceMetric::getResponseTimeMs)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        return round(average);
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

    private DowntimeEventResponse toResponse(DowntimeEvent event) {
        return new DowntimeEventResponse(
                event.getId(),
                event.getServiceId(),
                event.getStartedAt(),
                event.getEndedAt(),
                event.getDurationSeconds(),
                event.getEndedAt() == null
        );
    }
}
