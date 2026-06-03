package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.dto.MonitoredServiceRegistrationRequest;
import com.edgecloud.monitoring.dto.MonitoredServiceResponse;
import com.edgecloud.monitoring.entity.MonitoredService;
import com.edgecloud.monitoring.exception.DuplicateServiceException;
import com.edgecloud.monitoring.repository.MonitoredServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class MonitoredServiceServiceImpl implements MonitoredServiceService {

    private final MonitoredServiceRepository repository;

    public MonitoredServiceServiceImpl(MonitoredServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public MonitoredServiceResponse registerService(MonitoredServiceRegistrationRequest request) {
        if (repository.existsByServiceName(request.serviceName())) {
            throw new DuplicateServiceException("Service already registered: " + request.serviceName());
        }

        MonitoredService service = new MonitoredService();
        service.setServiceName(request.serviceName());
        service.setServiceUrl(request.serviceUrl());
        service.setStatus("UNKNOWN");

        MonitoredService saved = repository.save(service);

        return new MonitoredServiceResponse(
                saved.getId(),
                saved.getServiceName(),
                saved.getServiceUrl(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}