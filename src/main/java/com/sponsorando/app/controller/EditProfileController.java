package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.models.UserForm;
import com.sponsorando.app.services.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        // Fetch the user from the database
        UserAccount userAccount = userAccountService.getUser(currentUsername);

        // Convert UserAccount to UserForm
        UserForm userForm = new UserForm();
        userForm.setName(userAccount.getName());
        userForm.setEmail(userAccount.getEmail());
        userForm.setPassword(userAccount.getPassword());
        //TODO: implement image

        // Pass userForm to the view
        model.addAttribute("userForm", userForm);

        return "edit_profile";
    }


    @PostMapping("/update_profile")
    public String updateProfile(@Valid @ModelAttribute("userForm") UserForm userForm,
                                BindingResult result, Model model) {

        // If validation fails, return to form with errors
        if (result.hasErrors()) {
            model.addAttribute("userForm", userForm);
            return "edit_profile";
        }

        // Fetch the existing user from DB
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserAccount existingUser = userAccountService.getUser(currentUsername);

        // ✅ Keep password unchanged if user didn't modify it
        if (userForm.getPassword() == null || userForm.getPassword().isBlank()) {
            userForm.setPassword(existingUser.getPassword());
        }

        // Update user profile
        userAccountService.updateUserProfile(userForm);

        // Redirect to dashboard after successful update
        return "redirect:/user_dashboard";
    }


}
