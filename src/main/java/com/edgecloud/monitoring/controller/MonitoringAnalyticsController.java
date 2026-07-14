package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.MonitoringAnalyticsResponse;
import com.edgecloud.monitoring.service.MonitoringAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringAnalyticsController {

    private final MonitoringAnalyticsService monitoringAnalyticsService;

    public MonitoringAnalyticsController(MonitoringAnalyticsService monitoringAnalyticsService) {
        this.monitoringAnalyticsService = monitoringAnalyticsService;
    }

    @GetMapping("/analytics")
    public MonitoringAnalyticsResponse getAnalytics() {
        return monitoringAnalyticsService.getAnalytics();
    }
}
