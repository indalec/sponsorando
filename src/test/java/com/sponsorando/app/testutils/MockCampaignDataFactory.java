package com.sponsorando.app.testutils;

import com.sponsorando.app.models.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MockCampaignDataFactory {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    public List<Campaign> createMockCampaigns(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::createMockCampaign)
                .collect(Collectors.toList());
    }

    private Campaign createMockCampaign(int i) {
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        Campaign campaign = new Campaign();
        campaign.setId((long) i);
        campaign.setTitle("Campaign " + i);
        campaign.setSlug("campaign-" + i);
        campaign.setDescription("Description for campaign " + i);
        campaign.setStartDate(now.plusDays(i % 7 - 3)); // Start date: -3 to 3 days from now
        campaign.setEndDate(campaign.getStartDate().plusDays(i % 7)); // End date: 0 to 6 days after start date
        campaign.setGoalAmount(10000.0 + i * 1000);
        campaign.setCurrency(createMockCurrency());
        campaign.setCollectedAmount(0.0);
        campaign.setStatus(determineStatus(now, campaign.getStartDate(), campaign.getEndDate()));
        campaign.setShowLocation(true);
        campaign.setAddress(createMockAddress());
        campaign.setUserAccount(createMockUserAccount());
        campaign.setCategories(createMockCategories(2));
        campaign.setImages(createMockImages(3));
        campaign.setCreatedAt(now);
        campaign.setUpdatedAt(now);
        return campaign;
    }

    private CampaignStatus determineStatus(LocalDateTime now, LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(now)) {
            return CampaignStatus.COMPLETED; // For expired campaigns
        } else if (startDate.isAfter(now)) {
            return CampaignStatus.APPROVED; // For future campaigns
        } else {
            return CampaignStatus.ACTIVE; // For ongoing campaigns
        }
    }

    private Currency createMockCurrency() {
        Currency currency = new Currency();
        currency.setCode("EUR");
        currency.setSymbol("€");
        return currency;
    }

    private Address createMockAddress() {
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        Address address = new Address();
        address.setId(1L);
        address.setStreet("123 Test Street");
        address.setNumber("45");
        address.setCity("Test City");
        address.setCountry("Test Country");
        address.setPostcode("12345");
        address.setLatitude(40.7128);
        address.setLongitude(-74.0060);
        address.setCreatedAt(now);
        address.setUpdatedAt(now);
        return address;
    }

    private UserAccount createMockUserAccount() {
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1L);
        userAccount.setName("Test User");
        userAccount.setEmail("testuser@example.com");
        userAccount.setPassword("hashedpassword");
        userAccount.setRole(Role.USER);
        userAccount.setImageUrl("https://example.com/user-image.jpg");
        userAccount.setEnabled(true);
        userAccount.setCreatedAt(now);
        userAccount.setUpdatedAt(now);
        return userAccount;
    }

    private List<CampaignCategory> createMockCategories(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    CampaignCategory category = new CampaignCategory();
                    category.setId((long) i);
                    category.setName("Category " + i);
                    return category;
                })
                .collect(Collectors.toList());
    }

    private List<Image> createMockImages(int count) {
        LocalDateTime now = LocalDateTime.now(SYSTEM_ZONE);
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Image image = new Image();
                    image.setId((long) i);
                    image.setUrl("https://example.com/image" + i + ".jpg");
                    image.setAltText("Image " + i + " for campaign");
                    image.setCreatedAt(now);
                    return image;
                })
                .collect(Collectors.toList());
    }
}
