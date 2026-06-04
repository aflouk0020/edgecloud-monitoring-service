package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.HistoricalMetricsResponse;

public interface HistoricalMetricsService {

    HistoricalMetricsResponse getHistoricalMetrics();
}