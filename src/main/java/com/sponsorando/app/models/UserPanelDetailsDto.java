package com.sponsorando.app.models;

public class UserPanelDetailsDto {
    // User Info
    private Long id; //user id
    private String username;
    private String email;
    private Role role;
    private boolean enabled;

    // Campaigns Info
    private int totalCampaigns;
    private int draftCampaigns;
    private int activeCampaigns;
    private int inactiveCampaigns;
    private int frozenCampaigns;
    private int completedCampaigns;
    // TODO: if more Campaign statuses added

    // Donations Info
    private double totalDonationsCollectedInEuro; // for estimation purposes
    private double totalDonationsGivenInEuro; // for estimation purposes


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTotalCampaigns() {
        return totalCampaigns;
    }

    public void setTotalCampaigns(int totalCampaigns) {
        this.totalCampaigns = totalCampaigns;
    }

    public int getDraftCampaigns() {
        return draftCampaigns;
    }

    public void setDraftCampaigns(int draftCampaigns) {
        this.draftCampaigns = draftCampaigns;
    }

    public int getActiveCampaigns() {
        return activeCampaigns;
    }

    public void setActiveCampaigns(int activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }

    public int getInactiveCampaigns() {
        return inactiveCampaigns;
    }

    public void setInactiveCampaigns(int inactiveCampaigns) {
        this.inactiveCampaigns = inactiveCampaigns;
    }

    public int getFrozenCampaigns() {
        return frozenCampaigns;
    }

    public void setFrozenCampaigns(int frozenCampaigns) {
        this.frozenCampaigns = frozenCampaigns;
    }

    public int getCompletedCampaigns() {
        return completedCampaigns;
    }

    public void setCompletedCampaigns(int completedCampaigns) {
        this.completedCampaigns = completedCampaigns;
    }

    public double getTotalDonationsCollectedInEuro() {
        return totalDonationsCollectedInEuro;
    }

    public void setTotalDonationsCollectedInEuro(double totalDonationsCollectedInEuro) {
        this.totalDonationsCollectedInEuro = totalDonationsCollectedInEuro;
    }

    public double getTotalDonationsGivenInEuro() {
        return totalDonationsGivenInEuro;
    }

    public void setTotalDonationsGivenInEuro(double totalDonationsGivenInEuro) {
        this.totalDonationsGivenInEuro = totalDonationsGivenInEuro;
    }
}
