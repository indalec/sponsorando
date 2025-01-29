package com.sponsorando.app.models;

public enum PaymentStatus {
    SUCCEDED,
    FAILED,
    PENDING;

    public  static PaymentStatus[] getPaymentStatuses() {
        return PaymentStatus.values();
    }

}
