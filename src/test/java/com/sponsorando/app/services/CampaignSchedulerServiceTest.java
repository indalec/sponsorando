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
import java.time.ZoneOffset;
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
    private static final ZoneOffset UTC_ZONE = ZoneOffset.UTC;

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
    void testCheckAndUpdateCampaignStatus() {
        TestLogger.info("Starting testCheckAndUpdateCampaignStatus");
        AtomicInteger invocationCount = new AtomicInteger(0);

        when(campaignRepository.updateExpiredCampaigns(
                eq(CampaignStatus.ACTIVE), eq(CampaignStatus.COMPLETED), any(LocalDateTime.class), eq(BATCH_SIZE)))
                .thenAnswer(invocation -> {
                    int count = invocationCount.getAndIncrement();
                    return (count < 3) ? BATCH_SIZE : 572 % BATCH_SIZE; // Simulate updates
                });

        schedulerService.checkAndUpdateCampaignStatus();

        verify(campaignRepository, times(4)).updateExpiredCampaigns(
                eq(CampaignStatus.ACTIVE), eq(CampaignStatus.COMPLETED), localDateTimeCaptor.capture(), eq(BATCH_SIZE));

        List<LocalDateTime> capturedDateTimes = localDateTimeCaptor.getAllValues();
        LocalDateTime now = LocalDateTime.now(UTC_ZONE);
        capturedDateTimes.forEach(dateTime -> {
            assertTrue(dateTime.isBefore(now.plusSeconds(1)) && dateTime.isAfter(now.minusSeconds(1)));
        });

        assertEquals(4, invocationCount.get(), "updateExpiredCampaigns should be called 4 times");

        // Log results and end of the test
        TestLogger.info("Completed testCheckAndUpdateCampaignStatus with " + invocationCount.get() + " invocations.");
    }
}
