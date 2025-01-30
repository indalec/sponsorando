package com.sponsorando.app.controller;

import com.sponsorando.app.models.CampaignCategory;
import com.sponsorando.app.repositories.CampaignCategoryRepository;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CampaignController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;



    @GetMapping("/add_campaign")
    public String addCampaign(Model model) {
        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "add_campaign";
    }


}
