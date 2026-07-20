package com.edgecloud.monitoring.exception;

import java.util.UUID;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(UUID serviceId) {
        super("Monitored service not found: " + serviceId);
    }
}
