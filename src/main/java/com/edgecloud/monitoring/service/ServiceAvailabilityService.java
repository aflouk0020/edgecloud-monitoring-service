package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.DowntimeEventResponse;
import com.edgecloud.monitoring.dto.ServiceAvailabilityResponse;

import java.util.List;
import java.util.UUID;

public interface ServiceAvailabilityService {

    ServiceAvailabilityResponse getAvailability(UUID serviceId);

    List<DowntimeEventResponse> getDowntimeHistory(UUID serviceId);
}
