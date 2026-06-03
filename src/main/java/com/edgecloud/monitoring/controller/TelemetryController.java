package com.edgecloud.monitoring.controller;

import com.edgecloud.monitoring.dto.TelemetryRequest;
import com.edgecloud.monitoring.dto.TelemetryResponse;
import com.edgecloud.monitoring.service.TelemetryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<TelemetryResponse> receiveTelemetry(
            @Valid @RequestBody TelemetryRequest request) {

        TelemetryResponse response = telemetryService.saveTelemetry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{deviceId}")
    public List<TelemetryResponse> getTelemetryByDeviceId(@PathVariable String deviceId) {
        return telemetryService.getTelemetryByDeviceId(deviceId);
    }
}