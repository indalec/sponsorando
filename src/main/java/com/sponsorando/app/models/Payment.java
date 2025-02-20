package com.sponsorando.app.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "donation_id")
    private Long donationId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "donation_id", referencedColumnName = "id")
    @NotNull
    private Donation donation;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    private PaymentStatus paymentStatus;

    @Positive
    @NotNull
    private Double grossAmount;

    @PositiveOrZero
    private Double transactionFee;

    @PositiveOrZero
    private Double serviceFee;

    @Positive
    @NotNull
    private Double netAmount;

    private Double exchangeRatePaymentProvider;

    @ManyToOne
    @JoinColumn(name = "currency_code", referencedColumnName = "code")
    @NotNull
    private Currency currency;

    private Double exchangeRate;

    private Double netConvertedToCampaignCurrency;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentProvider paymentProvider;

    private String paymentMethod;

    private String failureMessage;

    private String invoiceId;

    @NotNull
    private LocalDateTime transactionDate;

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(Double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public Double getExchangeRatePaymentProvider() {
        return exchangeRatePaymentProvider;
    }

    public void setExchangeRatePaymentProvider(Double exchangeRatePaymentProvider) {
        this.exchangeRatePaymentProvider = exchangeRatePaymentProvider;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Double getNetConvertedToCampaignCurrency() {
        return netConvertedToCampaignCurrency;
    }

    public void setNetConvertedToCampaignCurrency(Double netConvertedToCampaignCurrency) {
        this.netConvertedToCampaignCurrency = netConvertedToCampaignCurrency;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "donationId=" + donationId +
                ", transactionId='" + transactionId + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", grossAmount=" + grossAmount +
                ", transactionFee=" + transactionFee +
                ", serviceFee=" + serviceFee +
                ", netAmount=" + netAmount +
                ", exchangeRatePaymentProvider=" + exchangeRatePaymentProvider +
                ", currency=" + currency +
                ", exchangeRate=" + exchangeRate +
                ", netConvertedToCampaignCurrency=" + netConvertedToCampaignCurrency +
                ", paymentProvider=" + paymentProvider +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", failureMessage='" + failureMessage + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
