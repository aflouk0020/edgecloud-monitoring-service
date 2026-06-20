package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoredServiceRegistrationRequest;
import com.edgecloud.monitoring.dto.MonitoredServiceResponse;

import java.util.List;

public interface MonitoredServiceService {

    MonitoredServiceResponse registerService(
            MonitoredServiceRegistrationRequest request);

    List<MonitoredServiceResponse> getAllServices();
}
