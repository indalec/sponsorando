package com.sponsorando.app.models;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Unsigned;

import java.time.LocalDateTime;
import java.util.List;

public class CampaignForm {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Please insert the date the campaign starts")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDateTime startDate;

    @NotNull(message = "Please insert the date the campaign ends")
    @FutureOrPresent(message = "Please select at least one option")
    private LocalDateTime endDate;

    @NotNull(message = "Please insert the amount your campaign needs to raise ")
    @Unsigned
    private Double goalAmount;

    @NotEmpty(message = "Please select at least one option")
    private List<CampaignCategory> categories;

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

    public List<CampaignCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<CampaignCategory> categories) {
        this.categories = categories;
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
}
