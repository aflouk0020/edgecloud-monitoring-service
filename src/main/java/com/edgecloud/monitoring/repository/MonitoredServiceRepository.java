package com.edgecloud.monitoring.repository;

import com.edgecloud.monitoring.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MonitoredServiceRepository
        extends JpaRepository<MonitoredService, UUID> {

    Optional<MonitoredService> findByServiceName(String serviceName);

    boolean existsByServiceName(String serviceName);
    
}