package com.edgecloud.monitoring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_metrics")
public class ServiceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID serviceId;

    private Long responseTimeMs;

    private Integer statusCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UptimeStatus uptimeStatus;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }

    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public UptimeStatus getUptimeStatus() { return uptimeStatus; }
    public void setUptimeStatus(UptimeStatus uptimeStatus) { this.uptimeStatus = uptimeStatus; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
}