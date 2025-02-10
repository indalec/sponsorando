package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UserDashboardController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/user_dashboard")
    public String userDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = authenticatedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        boolean isUser = authenticatedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_USER"));

        if (isAdmin) {
            return "redirect:/admin";
        } else if (isUser) {
            String email = principal.getName();
            UserAccount userAccount = userAccountService.getUser(email);
            model.addAttribute("userAccount", userAccount);
            return "user_dashboard";
        } else {
            return "redirect:/access_denied";
        }
    }
}
