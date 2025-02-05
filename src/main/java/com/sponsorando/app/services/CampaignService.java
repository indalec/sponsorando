package com.sponsorando.app.services;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        UserAccount userAccount = userAccountService.getUser(email);
        Campaign campaign = new Campaign();
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

    public Page<Campaign> getCampaigns(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return campaignRepository.findAll(pageable);
    }

    public Page<Campaign> getCampaignsByUserEmail(String email, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return campaignRepository.findByUserAccountEmailAndStatusNot(email, CampaignStatus.INACTIVE, pageable);
    }

}
