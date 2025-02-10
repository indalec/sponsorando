package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditProfileController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/edit_profile")
    public String editProfile(Model model) {
        String email = (String) model.getAttribute("username");

        if (email == null) {
            return "redirect:/login";
        }

        UserAccount userAccount = userAccountService.getUser(email);
        model.addAttribute("userAccount", userAccount);

        return "edit_profile";
    }

    @PostMapping("/update_profile")
    public String updateProfile(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "password", required = false) String password,
            Model model) {

        String email = (String) model.getAttribute("username");

        if (email == null) {
            return "redirect:/login";
        }

        UserAccount userAccount = userAccountService.getUser(email);
        userAccountService.updateUser(userAccount, name, password);

        return "redirect:/user_dashboard";
    }
}