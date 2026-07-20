package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.DowntimeEventResponse;
import com.edgecloud.monitoring.dto.ServiceAvailabilityResponse;
import com.edgecloud.monitoring.service.ServiceAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services/{serviceId}")
public class ServiceAvailabilityController {

    private final ServiceAvailabilityService serviceAvailabilityService;

    public ServiceAvailabilityController(
            ServiceAvailabilityService serviceAvailabilityService) {
        this.serviceAvailabilityService = serviceAvailabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<ServiceAvailabilityResponse> getAvailability(
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                serviceAvailabilityService.getAvailability(serviceId)
        );
    }

    @GetMapping("/downtime-history")
    public ResponseEntity<List<DowntimeEventResponse>> getDowntimeHistory(
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                serviceAvailabilityService.getDowntimeHistory(serviceId)
        );
    }
}
