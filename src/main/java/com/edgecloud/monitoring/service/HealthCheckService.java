package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoredServiceResponse;

import java.util.List;

public interface HealthCheckService {

    List<MonitoredServiceResponse> checkAllServices();
}