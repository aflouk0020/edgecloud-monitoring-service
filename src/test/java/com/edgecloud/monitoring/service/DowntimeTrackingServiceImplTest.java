package com.edgecloud.monitoring.service;

import com.edgecloud.monitoring.entity.DowntimeEvent;
import com.edgecloud.monitoring.entity.UptimeStatus;
import com.edgecloud.monitoring.repository.DowntimeEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DowntimeTrackingServiceImplTest {

    @Mock
    private DowntimeEventRepository downtimeEventRepository;

    @InjectMocks
    private DowntimeTrackingServiceImpl downtimeTrackingService;

    @Test
    void opensDowntimeEventWhenServiceChangesFromUpToDown() {
        UUID serviceId = UUID.randomUUID();
        LocalDateTime checkedAt = LocalDateTime.of(2026, 7, 20, 14, 0);

        when(downtimeEventRepository
                .findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(serviceId))
                .thenReturn(Optional.empty());

        downtimeTrackingService.recordStatusTransition(
                serviceId,
                "UP",
                UptimeStatus.DOWN,
                checkedAt
        );

        ArgumentCaptor<DowntimeEvent> eventCaptor =
                ArgumentCaptor.forClass(DowntimeEvent.class);

        verify(downtimeEventRepository).save(eventCaptor.capture());

        DowntimeEvent savedEvent = eventCaptor.getValue();

        assertThat(savedEvent.getServiceId()).isEqualTo(serviceId);
        assertThat(savedEvent.getStartedAt()).isEqualTo(checkedAt);
        assertThat(savedEvent.getEndedAt()).isNull();
        assertThat(savedEvent.getDurationSeconds()).isNull();
    }

    @Test
    void opensDowntimeEventWhenInitialUnknownServiceIsDown() {
        UUID serviceId = UUID.randomUUID();
        LocalDateTime checkedAt = LocalDateTime.of(2026, 7, 20, 14, 5);

        when(downtimeEventRepository
                .findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(serviceId))
                .thenReturn(Optional.empty());

        downtimeTrackingService.recordStatusTransition(
                serviceId,
                "UNKNOWN",
                UptimeStatus.DOWN,
                checkedAt
        );

        verify(downtimeEventRepository).save(
                org.mockito.ArgumentMatchers.any(DowntimeEvent.class)
        );
    }

    @Test
    void doesNotCreateDuplicateEventWhenServiceRemainsDown() {
        downtimeTrackingService.recordStatusTransition(
                UUID.randomUUID(),
                "DOWN",
                UptimeStatus.DOWN,
                LocalDateTime.now()
        );

        verifyNoInteractions(downtimeEventRepository);
    }

    @Test
    void closesActiveDowntimeEventWhenServiceRecovers() {
        UUID serviceId = UUID.randomUUID();
        LocalDateTime startedAt = LocalDateTime.of(2026, 7, 20, 14, 0);
        LocalDateTime recoveredAt = startedAt.plusSeconds(90);

        DowntimeEvent activeEvent = new DowntimeEvent();
        activeEvent.setServiceId(serviceId);
        activeEvent.setStartedAt(startedAt);

        when(downtimeEventRepository
                .findFirstByServiceIdAndEndedAtIsNullOrderByStartedAtDesc(serviceId))
                .thenReturn(Optional.of(activeEvent));

        downtimeTrackingService.recordStatusTransition(
                serviceId,
                "DOWN",
                UptimeStatus.UP,
                recoveredAt
        );

        verify(downtimeEventRepository).save(activeEvent);

        assertThat(activeEvent.getEndedAt()).isEqualTo(recoveredAt);
        assertThat(activeEvent.getDurationSeconds()).isEqualTo(90L);
    }

    @Test
    void doesNothingWhenServiceRemainsUp() {
        downtimeTrackingService.recordStatusTransition(
                UUID.randomUUID(),
                "UP",
                UptimeStatus.UP,
                LocalDateTime.now()
        );

        verifyNoInteractions(downtimeEventRepository);
    }
}
