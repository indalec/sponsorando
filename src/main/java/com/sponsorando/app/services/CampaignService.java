package com.sponsorando.app.services;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
public class CampaignService {

    @Autowired
    UserAccountService userAccountService;
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private AddressService addressService;

    @Transactional
    public Campaign createCampaign(CampaignForm campaignForm, String email) {

        System.out.println("Campaign Form:::" + campaignForm);
        Campaign campaign = new Campaign();
        UserAccount userAccount = userAccountService.getUser(email);


        campaign.setTitle(campaignForm.getTitle());
        campaign.setDescription(campaignForm.getDescription());
        campaign.setStartDate(campaignForm.getStartDate());
        campaign.setEndDate(campaignForm.getEndDate());
        campaign.setGoalAmount(campaignForm.getGoalAmount());
        campaign.setCollectedAmount(0.0);
        List<CampaignCategory> categories = campaignForm.getCategories();
        campaign.setCategories(categories);
        campaign.setShowLocation(Boolean.TRUE);

        if (userAccount != null) {

            campaign.setUserAccount(userAccount);
            Address address = addressService.createAddress(campaignForm);

            if (address != null) {
                campaign.setAddress(address);
                return campaignRepository.save(campaign);
            }

        }
        return null;
    }
}
