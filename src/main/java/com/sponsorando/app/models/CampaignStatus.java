package com.sponsorando.app.models;

public enum CampaignStatus {

    DRAFT,
    PENDING,
    APPROVED,
    DECLINED,
    ACTIVE,
    INACTIVE,
    FROZEN,
    COMPLETED;

    public static CampaignStatus[] getCampaignStatuses() {
        return CampaignStatus.values();
    }
}


