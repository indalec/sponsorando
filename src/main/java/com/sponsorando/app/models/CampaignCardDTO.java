package com.sponsorando.app.models;
import java.util.List;

public class CampaignCardDTO {
    private String slug;
    private String title;
    private List<CampaignCategoryDTO> categories;
    private String description;
    private Double collectedAmount;
    private Double goalAmount;
    private CurrencyDTO currency;
    private boolean showLocation;
    private AddressDTO address;

    public CampaignCardDTO() {
    }

    public CampaignCardDTO(String slug, String title, List<CampaignCategoryDTO> categories, String description, Double collectedAmount, Double goalAmount, CurrencyDTO currency, boolean showLocation, AddressDTO address) {
        this.slug = slug;
        this.title = title;
        this.categories = categories;
        this.description = description;
        this.collectedAmount = collectedAmount;
        this.goalAmount = goalAmount;
        this.currency = currency;
        this.showLocation = showLocation;
        this.address = address;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CampaignCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CampaignCategoryDTO> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(Double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public Double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(Double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public CurrencyDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDTO currency) {
        this.currency = currency;
    }

    public boolean isShowLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}


