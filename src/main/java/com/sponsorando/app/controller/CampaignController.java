package com.sponsorando.app.controller;

import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CampaignController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/add_campaign")
    public String addCampaign(){
        return "add_campaign";
    }


}
