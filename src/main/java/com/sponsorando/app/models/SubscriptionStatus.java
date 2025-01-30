package com.sponsorando.app.models;

public enum SubscriptionStatus {

    ON,
    PAUSE,
    OFF;

    public static SubscriptionStatus[] getSubscriptionStatuses() {
        return SubscriptionStatus.values();
    }
}
