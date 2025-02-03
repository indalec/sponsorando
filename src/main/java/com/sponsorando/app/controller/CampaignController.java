package com.sponsorando.app.controller;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignCategory;
import com.sponsorando.app.models.CampaignForm;
import com.sponsorando.app.repositories.CampaignCategoryRepository;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.services.CampaignService;
import com.sponsorando.app.services.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CampaignController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;

    @Autowired
    private CampaignService campaignService;



    @GetMapping("/add_campaign")
    public String addCampaign(Model model) {
        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "add_campaign";
    }

    @PostMapping("/add_campaign")
    public String addCampaign(@ModelAttribute @Valid CampaignForm campaignForm, Model model) {

        System.out.println("COME HERE");

        System.out.println("Campaign Details::::::"+campaignForm);

        String email = (String) model.getAttribute("username");


        if(email != null) {

            try {
                Campaign campaign = campaignService.createCampaign(campaignForm, email);
                if(campaign != null) {
                    model.addAttribute("campaign", campaign);
                    model.addAttribute("isCampaignAdded", true);
                }else{
                    model.addAttribute("isCampaignAdded", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("isCampaignAdded", false);
            }
        }

        return "add_campaign";
    }
}
