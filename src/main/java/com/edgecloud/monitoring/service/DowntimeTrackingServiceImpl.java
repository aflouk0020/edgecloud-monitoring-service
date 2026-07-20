package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.entity.DowntimeEvent;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.repository.DowntimeEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DowntimeTrackingServiceImpl implements DowntimeTrackingService {

    private final DowntimeEventRepository downtimeEventRepository;

    public DowntimeTrackingServiceImpl(
            DowntimeEventRepository downtimeEventRepository) {
        this.downtimeEventRepository = downtimeEventRepository;
    }

    @Override
    @Transactional
    public void recordStatusTransition(
            UUID serviceId,
            String previousStatus,
            UptimeStatus currentStatus,
            LocalDateTime checkedAt) {

        if (currentStatus == UptimeStatus.DOWN
                && !"DOWN".equalsIgnoreCase(previousStatus)) {
            openDowntimeEvent(serviceId, checkedAt);
            return;
        }

        if (currentStatus == UptimeStatus.UP
                && "DOWN".equalsIgnoreCase(previousStatus)) {
            closeDowntimeEvent(serviceId, checkedAt);
        }
    }

    private void openDowntimeEvent(UUID serviceId, LocalDateTime startedAt) {
        boolean activeEventExists = downtimeEventRepository
                .findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(serviceId)
                .isPresent();

        if (activeEventExists) {
            return;
        }

        DowntimeEvent event = new DowntimeEvent();
        event.setServiceId(serviceId);
        event.setStartedAt(startedAt);

        downtimeEventRepository.save(event);
    }

    private void closeDowntimeEvent(UUID serviceId, LocalDateTime endedAt) {
        downtimeEventRepository
                .findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(serviceId)
                .ifPresent(event -> {
                    long durationSeconds = Math.max(
                            0,
                            Duration.between(event.getStartedAt(), endedAt).getSeconds()
                    );

                    event.setEndedAt(endedAt);
                    event.setDurationSeconds(durationSeconds);

                    downtimeEventRepository.save(event);
                });
    }
}
