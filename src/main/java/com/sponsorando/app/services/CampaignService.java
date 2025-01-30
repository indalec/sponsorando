package com.sponsorando.app.services;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignForm;
import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CampaignService {

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    private CampaignRepository campaignRepository;


    public Campaign createCampaign(CampaignForm campaignForm, Principal principal) {
        Campaign campaign = new Campaign();
        UserAccount userAccount = userAccountService.getUser(principal);


        campaign.setTitle(campaignForm.getTitle());
        campaign.setDescription(campaignForm.getDescription());
        campaign.setStartDate(campaignForm.getStartDate());
        campaign.setEndDate(campaignForm.getEndDate());
        campaign.setGoalAmount(campaignForm.getGoalAmount());
        campaign.setCollectedAmount(0.0);
        campaign.setCategories(campaignForm.getCategories());

        if(userAccount != null) {
            campaign.setUserAccount(userAccount);
            return campaignRepository.save(campaign);
        }

        return null;
    }
}
