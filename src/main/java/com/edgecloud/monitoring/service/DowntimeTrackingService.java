package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.entity.UptimeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DowntimeTrackingService {

    void recordStatusTransition(
            UUID serviceId,
            String previousStatus,
            UptimeStatus currentStatus,
            LocalDateTime checkedAt
    );
}
