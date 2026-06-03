package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.TelemetryRequest;
import com.edgecloud.monitoring.dto.TelemetryResponse;

import java.util.List;

public interface TelemetryService {

    TelemetryResponse saveTelemetry(TelemetryRequest request);

    List<TelemetryResponse> getTelemetryByDeviceId(String deviceId);
}