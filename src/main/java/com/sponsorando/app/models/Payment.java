package com.sponsorando.app.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @OneToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Double grossAmount;

    private Double transactionFee;

    private Double serviceFee;

    private Double netAmount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    private String paymentMethod;

    private String failureMessage;

    private String invoiceId;

    private LocalDateTime transactionDate;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
                "donation=" + donation +
                ", transactionId='" + transactionId + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", grossAmount=" + grossAmount +
                ", transactionFee=" + transactionFee +
                ", serviceFee=" + serviceFee +
                ", netAmount=" + netAmount +
                ", currency='" + currency + '\'' +
                ", paymentProvider=" + paymentProvider +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", failureMessage='" + failureMessage + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
