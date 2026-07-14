package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoringAnalyticsResponse;
import com.edgecloud.monitoring.entity.ServiceMetric;
import com.edgecloud.monitoring.entity.TelemetryMetric;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.repository.ServiceMetricRepository;
import com.edgecloud.monitoring.repository.TelemetryMetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonitoringAnalyticsServiceImplTest {

    @Mock
    private ServiceMetricRepository serviceMetricRepository;

    @Mock
    private TelemetryMetricRepository telemetryMetricRepository;

    @InjectMocks
    private MonitoringAnalyticsServiceImpl analyticsService;

    @Test
    void calculatesServiceAndTelemetryAnalytics() {
        when(serviceMetricRepository.findAll()).thenReturn(List.of(
                serviceMetric(UptimeStatus.UP, 100L),
                serviceMetric(UptimeStatus.UP, 200L),
                serviceMetric(UptimeStatus.DOWN, 600L)
        ));

        when(telemetryMetricRepository.findAll()).thenReturn(List.of(
                telemetryMetric(20.0, 40.0, 50.0),
                telemetryMetric(40.0, 60.0, 70.0)
        ));

        MonitoringAnalyticsResponse response = analyticsService.getAnalytics();

        assertThat(response.totalServiceChecks()).isEqualTo(3);
        assertThat(response.upServiceChecks()).isEqualTo(2);
        assertThat(response.downServiceChecks()).isEqualTo(1);
        assertThat(response.serviceUptimePercentage()).isEqualTo(66.67);
        assertThat(response.averageResponseTimeMs()).isEqualTo(300.0);
        assertThat(response.telemetrySamples()).isEqualTo(2);
        assertThat(response.averageCpuUsage()).isEqualTo(30.0);
        assertThat(response.averageMemoryUsage()).isEqualTo(50.0);
        assertThat(response.averageTemperature()).isEqualTo(60.0);
    }

    @Test
    void returnsZeroValuesWhenNoMetricsExist() {
        when(serviceMetricRepository.findAll()).thenReturn(List.of());
        when(telemetryMetricRepository.findAll()).thenReturn(List.of());

        MonitoringAnalyticsResponse response = analyticsService.getAnalytics();

        assertThat(response.totalServiceChecks()).isZero();
        assertThat(response.serviceUptimePercentage()).isZero();
        assertThat(response.averageResponseTimeMs()).isZero();
        assertThat(response.telemetrySamples()).isZero();
        assertThat(response.averageCpuUsage()).isZero();
        assertThat(response.averageMemoryUsage()).isZero();
        assertThat(response.averageTemperature()).isZero();
    }

    private ServiceMetric serviceMetric(UptimeStatus status, Long responseTimeMs) {
        ServiceMetric metric = new ServiceMetric();
        metric.setUptimeStatus(status);
        metric.setResponseTimeMs(responseTimeMs);
        return metric;
    }

    private TelemetryMetric telemetryMetric(double cpu, double memory, double temperature) {
        TelemetryMetric metric = new TelemetryMetric();
        metric.setCpuUsage(cpu);
        metric.setMemoryUsage(memory);
        metric.setTemperature(temperature);
        return metric;
    }
}
