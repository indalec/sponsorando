package com.sponsorando.app.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class DonationForm {

    @NotNull(message = "Please add the amount you wish to donate")
    @NotNull(message = "Please select an amount")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Please select a currency")
    private Currency currency;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime donationDate;

    private Long campaignId;

    private Long userId;

    private boolean anonymous;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDateTime getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDateTime donationDate) {
        this.donationDate = donationDate;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    @Override
    public String toString() {
        return "DonationForm{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", donationDate=" + donationDate +
                ", campaignId=" + campaignId +
                ", userId=" + userId +
                ", anonymous=" + anonymous +
                '}';
    }
}
