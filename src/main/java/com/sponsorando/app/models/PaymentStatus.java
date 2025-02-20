package com.sponsorando.app.models;

public enum PaymentStatus {
    SUCCEEDED,
    FAILED,
    PENDING;

    public  static PaymentStatus[] getPaymentStatuses() {
        return PaymentStatus.values();
    }

}
