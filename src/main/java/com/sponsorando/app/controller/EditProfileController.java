package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class EditProfileController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/edit_profile")
    public String showEditProfileForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserAccount userAccount = userAccountService.getUser(currentUsername);

        model.addAttribute("userAccount", userAccount);
        return "edit_profile";
    }

    @PostMapping("/update_profile")
    public String updateProfile(@ModelAttribute("userAccount") UserAccount userAccount) {
        userAccountService.updateUserProfile(userAccount);
        return "redirect:/user_dashboard";
    }
}
