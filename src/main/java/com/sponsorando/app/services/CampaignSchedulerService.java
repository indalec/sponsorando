package com.sponsorando.app.services;

import com.sponsorando.app.models.CampaignStatus;
import com.sponsorando.app.repositories.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class CampaignSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignSchedulerService.class);
    private static final ZoneOffset UTC_ZONE = ZoneOffset.UTC;

    public static final String BATCH_SIZE = "app.scheduler.expired.campaigns.batch.size";
    public static final String FIXED_RATE = "app.scheduler.expired.campaigns.rate";
    public static final String INITIAL_DELAY = "app.scheduler.expired.campaigns.initial.delay";

    private final int batchSize;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final long fixedRate;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final long initialDelay;
    private final CampaignRepository campaignRepository;

    public CampaignSchedulerService(CampaignRepository campaignRepository,
                                    @Value("${" + BATCH_SIZE + ":1000}") int batchSize,
                                    @Value("${" + FIXED_RATE + ":60000}") long fixedRate,
                                    @Value("${" + INITIAL_DELAY + ":60000}") long initialDelay) {
        this.campaignRepository = campaignRepository;
        this.batchSize = batchSize;
        this.fixedRate = fixedRate;
        this.initialDelay = initialDelay;
    }

    @Scheduled(initialDelayString = "${" + INITIAL_DELAY + ":60000}",
            fixedRateString = "${" + FIXED_RATE + ":60000}")
    @Transactional
    public void checkAndUpdateCampaignStatus() {
        LocalDateTime now = LocalDateTime.now(UTC_ZONE);
        int totalUpdated = 0;
        int updatedCount;

        do {
            updatedCount = campaignRepository.updateExpiredCampaigns(
                    CampaignStatus.ACTIVE, CampaignStatus.COMPLETED, now, batchSize);
            totalUpdated += updatedCount;
        } while (updatedCount == batchSize);

        logger.info("Updated {} expired campaigns", totalUpdated);
    }
}
