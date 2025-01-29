package com.sponsorando.app.models;

public enum SubscriptionType {

    DAY,
    WEEK,
    MONTH,
    YEAR;

    public static SubscriptionType[] getSubscriptionTypes() {
        return SubscriptionType.values();
    }


}