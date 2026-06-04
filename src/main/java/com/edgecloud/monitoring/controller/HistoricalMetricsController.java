package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.HistoricalMetricsResponse;
import com.edgecloud.monitoring.service.HistoricalMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoricalMetricsController {

    private final HistoricalMetricsService historicalMetricsService;

    public HistoricalMetricsController(HistoricalMetricsService historicalMetricsService) {
        this.historicalMetricsService = historicalMetricsService;
    }

    @GetMapping("/history")
    public HistoricalMetricsResponse getHistoricalMetrics() {
        return historicalMetricsService.getHistoricalMetrics();
    }
}