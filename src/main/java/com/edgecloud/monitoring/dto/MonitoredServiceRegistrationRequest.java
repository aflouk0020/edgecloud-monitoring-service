package com.edgecloud.monitoring.dto;

import jakarta.validation.constraints.NotBlank;

public record MonitoredServiceRegistrationRequest(

        @NotBlank(message = "Service name is required")
        String serviceName,

        @NotBlank(message = "Service URL is required")
        String serviceUrl

) {
}