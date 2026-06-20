package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.MonitoredServiceRegistrationRequest;
import com.edgecloud.monitoring.dto.MonitoredServiceResponse;
import com.edgecloud.monitoring.service.MonitoredServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class MonitoredServiceController {

    private final MonitoredServiceService monitoredServiceService;

    public MonitoredServiceController(MonitoredServiceService monitoredServiceService) {
        this.monitoredServiceService = monitoredServiceService;
    }

    @PostMapping
    public ResponseEntity<MonitoredServiceResponse> registerService(
            @Valid @RequestBody MonitoredServiceRegistrationRequest request) {

        MonitoredServiceResponse response = monitoredServiceService.registerService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MonitoredServiceResponse>> getServices() {
        return ResponseEntity.ok(monitoredServiceService.getAllServices());
    }
}
