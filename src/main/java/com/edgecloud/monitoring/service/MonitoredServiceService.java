package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoredServiceRegistrationRequest;
import com.edgecloud.monitoring.dto.MonitoredServiceResponse;

public interface MonitoredServiceService {

    MonitoredServiceResponse registerService(
            MonitoredServiceRegistrationRequest request);

}