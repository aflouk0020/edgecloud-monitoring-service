package com.edgecloud.monitoring.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelemetryRequest(

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotNull(message = "CPU usage is required")
        @Min(value = 0, message = "CPU usage cannot be below 0")
        @Max(value = 100, message = "CPU usage cannot exceed 100")
        Double cpuUsage,

        @NotNull(message = "Memory usage is required")
        @Min(value = 0, message = "Memory usage cannot be below 0")
        @Max(value = 100, message = "Memory usage cannot exceed 100")
        Double memoryUsage,

        @NotNull(message = "Temperature is required")
        Double temperature
) {
}