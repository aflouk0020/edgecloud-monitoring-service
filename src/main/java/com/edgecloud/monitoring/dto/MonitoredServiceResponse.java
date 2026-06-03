package com.edgecloud.monitoring.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MonitoredServiceResponse(

        UUID id,
        String serviceName,
        String serviceUrl,
        String status,
        LocalDateTime createdAt

) {
}