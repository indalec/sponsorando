package com.sponsorando.app.models;

public enum CampaignStatus {

    DRAFT,
    ACTIVE,
    INACTIVE,
    COMPLETED;

    public static CampaignStatus[] getCampaignStatuses() {
        return CampaignStatus.values();
    }
}


