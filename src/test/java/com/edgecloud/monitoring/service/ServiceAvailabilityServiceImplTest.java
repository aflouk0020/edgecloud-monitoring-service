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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceAvailabilityServiceImplTest {

    @Mock
    private MonitoredServiceRepository monitoredServiceRepository;

    @Mock
    private ServiceMetricRepository serviceMetricRepository;

    @Mock
    private DowntimeEventRepository downtimeEventRepository;

    @InjectMocks
    private ServiceAvailabilityServiceImpl availabilityService;

    @Test
    void calculatesAvailabilityAndDowntimeSummary() {
        UUID serviceId = UUID.randomUUID();

        MonitoredService service = monitoredService(
                serviceId,
                "Device Service",
                "UP"
        );

        List<ServiceMetric> metrics = List.of(
                serviceMetric(UptimeStatus.UP, 100L),
                serviceMetric(UptimeStatus.UP, 200L),
                serviceMetric(UptimeStatus.DOWN, 600L)
        );

        DowntimeEvent latestEvent = downtimeEvent(
                serviceId,
                LocalDateTime.of(2026, 7, 20, 15, 0),
                LocalDateTime.of(2026, 7, 20, 15, 1),
                60L
        );

        DowntimeEvent olderEvent = downtimeEvent(
                serviceId,
                LocalDateTime.of(2026, 7, 20, 14, 0),
                LocalDateTime.of(2026, 7, 20, 14, 2),
                120L
        );

        when(monitoredServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(service));

        when(serviceMetricRepository
                .findByServiceIdOrderByRecordedAtDesc(serviceId))
                .thenReturn(metrics);

        when(downtimeEventRepository
                .findByServiceIdOrderByStartedAtDesc(serviceId))
                .thenReturn(List.of(latestEvent, olderEvent));

        ServiceAvailabilityResponse response =
                availabilityService.getAvailability(serviceId);

        assertThat(response.serviceId()).isEqualTo(serviceId);
        assertThat(response.serviceName()).isEqualTo("Device Service");
        assertThat(response.currentStatus()).isEqualTo("UP");
        assertThat(response.totalChecks()).isEqualTo(3);
        assertThat(response.upChecks()).isEqualTo(2);
        assertThat(response.downChecks()).isEqualTo(1);
        assertThat(response.uptimePercentage()).isEqualTo(66.67);
        assertThat(response.averageResponseTimeMs()).isEqualTo(300.0);
        assertThat(response.downtimeEventCount()).isEqualTo(2);
        assertThat(response.totalDowntimeSeconds()).isEqualTo(180);
        assertThat(response.lastDowntimeStartedAt())
                .isEqualTo(latestEvent.getStartedAt());
        assertThat(response.lastRecoveredAt())
                .isEqualTo(latestEvent.getEndedAt());
        assertThat(response.currentlyDown()).isFalse();
    }

    @Test
    void returnsEmptyAvailabilitySummaryWhenNoHistoryExists() {
        UUID serviceId = UUID.randomUUID();

        MonitoredService service = monitoredService(
                serviceId,
                "New Service",
                "UNKNOWN"
        );

        when(monitoredServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(service));

        when(serviceMetricRepository
                .findByServiceIdOrderByRecordedAtDesc(serviceId))
                .thenReturn(List.of());

        when(downtimeEventRepository
                .findByServiceIdOrderByStartedAtDesc(serviceId))
                .thenReturn(List.of());

        ServiceAvailabilityResponse response =
                availabilityService.getAvailability(serviceId);

        assertThat(response.totalChecks()).isZero();
        assertThat(response.upChecks()).isZero();
        assertThat(response.downChecks()).isZero();
        assertThat(response.uptimePercentage()).isZero();
        assertThat(response.averageResponseTimeMs()).isZero();
        assertThat(response.downtimeEventCount()).isZero();
        assertThat(response.totalDowntimeSeconds()).isZero();
        assertThat(response.lastDowntimeStartedAt()).isNull();
        assertThat(response.lastRecoveredAt()).isNull();
        assertThat(response.currentlyDown()).isFalse();
    }

    @Test
    void mapsDowntimeHistoryAndActiveState() {
        UUID serviceId = UUID.randomUUID();

        MonitoredService service = monitoredService(
                serviceId,
                "Alert Service",
                "DOWN"
        );

        DowntimeEvent activeEvent = downtimeEvent(
                serviceId,
                LocalDateTime.of(2026, 7, 20, 16, 0),
                null,
                null
        );

        DowntimeEvent closedEvent = downtimeEvent(
                serviceId,
                LocalDateTime.of(2026, 7, 20, 15, 0),
                LocalDateTime.of(2026, 7, 20, 15, 3),
                180L
        );

        when(monitoredServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(service));

        when(downtimeEventRepository
                .findByServiceIdOrderByStartedAtDesc(serviceId))
                .thenReturn(List.of(activeEvent, closedEvent));

        List<DowntimeEventResponse> history =
                availabilityService.getDowntimeHistory(serviceId);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).active()).isTrue();
        assertThat(history.get(0).endedAt()).isNull();
        assertThat(history.get(1).active()).isFalse();
        assertThat(history.get(1).durationSeconds()).isEqualTo(180L);
    }

    @Test
    void throwsExceptionWhenServiceDoesNotExist() {
        UUID serviceId = UUID.randomUUID();

        when(monitoredServiceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> availabilityService.getAvailability(serviceId)
        )
                .isInstanceOf(ServiceNotFoundException.class)
                .hasMessage("Monitored service not found: " + serviceId);
    }

    private MonitoredService monitoredService(
            UUID id,
            String name,
            String status) {

        MonitoredService service = new MonitoredService();
        service.setId(id);
        service.setServiceName(name);
        service.setServiceUrl("http://localhost");
        service.setStatus(status);

        return service;
    }

    private ServiceMetric serviceMetric(
            UptimeStatus status,
            Long responseTimeMs) {

        ServiceMetric metric = new ServiceMetric();
        metric.setUptimeStatus(status);
        metric.setResponseTimeMs(responseTimeMs);

        return metric;
    }

    private DowntimeEvent downtimeEvent(
            UUID serviceId,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Long durationSeconds) {

        DowntimeEvent event = new DowntimeEvent();
        event.setServiceId(serviceId);
        event.setStartedAt(startedAt);
        event.setEndedAt(endedAt);
        event.setDurationSeconds(durationSeconds);

        return event;
    }
}
