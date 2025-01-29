package com.sponsorando.app.models;

public enum PaymentProvider {

    STRIPE,
    OTHER;


    public static PaymentProvider[] getPaymentProviders() {

        return PaymentProvider.values();

    }
}
