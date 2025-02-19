package com.sponsorando.app.models;

import jakarta.validation.constraints.*;
import jdk.jfr.Unsigned;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class CampaignForm {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Please insert the date the campaign starts")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "Please insert the date the campaign ends")
    @FutureOrPresent(message = "Please select at least one option")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endDate;

    @NotNull(message = "Please insert the amount your campaign needs to raise ")
    @Unsigned
    private Double goalAmount;

    @NotNull(message = "Please select a currency for your goal amount")
    private Currency currency;

    @NotEmpty(message = "Please select at least one option")
    private List<CampaignCategory> categories;

    private Boolean showLocation;

    private int page;

    private String status;

    private Long campaignId;

    @NotBlank(message = "Street is mandatory")
    private String street;

    @NotBlank(message = "Street Number is mandatory")
    private String number;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "Country is mandatory")
    private String country;

    @NotBlank(message = "Postcode is mandatory")
    private String postcode;

    private Double latitude;

    private Double longitude;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(Double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<CampaignCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<CampaignCategory> categories) {
        this.categories = categories;
    }

    public Boolean getShowLocation() {
        return showLocation;
    }

    public void setShowLocation(Boolean showLocation) {
        this.showLocation = showLocation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CampaignForm{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", goalAmount=" + goalAmount +
                ", currency=" + currency +
                ", categories=" + categories +
                ", showLocation=" + showLocation +
                ", page=" + page +
                ", status='" + status + '\'' +
                ", campaignId=" + campaignId +
                ", street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", postcode='" + postcode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
