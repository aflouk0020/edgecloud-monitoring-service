package com.edgecloud.monitoring.repository;

import com.edgecloud.monitoring.entity.DowntimeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DowntimeEventRepository extends JpaRepository<DowntimeEvent, UUID> {

    List<DowntimeEvent> findByServiceIdOrderByStartedAtDesc(UUID serviceId);

    List<DowntimeEvent> findAllByOrderByStartedAtDesc();

    Optional<DowntimeEvent> findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(
            UUID serviceId
    );
}
