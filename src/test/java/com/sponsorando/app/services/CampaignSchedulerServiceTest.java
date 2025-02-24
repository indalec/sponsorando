package com.sponsorando.app.services;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignStatus;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.testutils.MockCampaignDataFactory;
import com.sponsorando.app.testutils.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignSchedulerServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    private CampaignSchedulerService schedulerService;

    private static final int BATCH_SIZE = 1000;
    public static final int FIXED_RATE = 6000;
    public static final int INITIAL_DELAY = 6000;
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private List<Campaign> mockCampaigns;

    @Captor
    private ArgumentCaptor<LocalDateTime> localDateTimeCaptor;

    @BeforeEach
    void setUp() {
        MockCampaignDataFactory mockDataFactory = new MockCampaignDataFactory();
        schedulerService = new CampaignSchedulerService(campaignRepository, BATCH_SIZE, FIXED_RATE, INITIAL_DELAY);
        mockCampaigns = mockDataFactory.createMockCampaigns(BATCH_SIZE * 3 + 123);
    }

    @Test
    void testUpdateExpiredCampaigns() {
        TestLogger.info("TEST-SCHEDULER: Starting testUpdateExpiredCampaigns");
        AtomicInteger invocationCount = new AtomicInteger(0);

        when(campaignRepository.updateExpiredCampaigns(
                eq(CampaignStatus.ACTIVE), eq(CampaignStatus.COMPLETED), any(LocalDateTime.class), eq(BATCH_SIZE)))
                .thenAnswer(invocation -> {
                    int count = invocationCount.getAndIncrement();
                    return (count < 3) ? BATCH_SIZE : 572 % BATCH_SIZE; // Simulate updates
                });

        schedulerService.updateExpiredCampaigns();

        verify(campaignRepository, times(4)).updateExpiredCampaigns(
                eq(CampaignStatus.ACTIVE), eq(CampaignStatus.COMPLETED), localDateTimeCaptor.capture(), eq(BATCH_SIZE));

        List<LocalDateTime> capturedDateTimes = localDateTimeCaptor.getAllValues();
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        capturedDateTimes.forEach(dateTime -> {
            assertTrue(dateTime.isBefore(now.plusSeconds(1)) && dateTime.isAfter(now.minusSeconds(1)));
        });

        assertEquals(4, invocationCount.get(), "updateExpiredCampaigns should be called 4 times");

        TestLogger.info("TEST-SCHEDULER: Completed testUpdateExpiredCampaigns with " + invocationCount.get() + " invocations.");
    }

    @Test
    void testActivateApprovedCampaigns() {
        TestLogger.info("TEST-SCHEDULER: Starting testActivateApprovedCampaigns");
        AtomicInteger invocationCount = new AtomicInteger(0);

        when(campaignRepository.activateApprovedCampaigns(
                eq(CampaignStatus.APPROVED), eq(CampaignStatus.ACTIVE), any(LocalDateTime.class), eq(BATCH_SIZE)))
                .thenAnswer(invocation -> {
                    int count = invocationCount.getAndIncrement();
                    return (count < 2) ? BATCH_SIZE : 500 % BATCH_SIZE; // Simulate activations
                });

        schedulerService.activateApprovedCampaigns();

        verify(campaignRepository, times(3)).activateApprovedCampaigns(
                eq(CampaignStatus.APPROVED), eq(CampaignStatus.ACTIVE), localDateTimeCaptor.capture(), eq(BATCH_SIZE));

        List<LocalDateTime> capturedDateTimes = localDateTimeCaptor.getAllValues();
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        capturedDateTimes.forEach(dateTime -> {
            assertTrue(dateTime.isBefore(now.plusSeconds(1)) && dateTime.isAfter(now.minusSeconds(1)));
        });

        assertEquals(3, invocationCount.get(), "activateApprovedCampaigns should be called 3 times");

        TestLogger.info("TEST-SCHEDULER: Completed testActivateApprovedCampaigns with " + invocationCount.get() + " invocations.");
    }
}
