package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.models.UserEditForm;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class EditProfileController {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/edit_profile")
    public String showEditProfileForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        UserAccount userAccount = userAccountService.getUser(currentUsername);
        UserEditForm userEditForm = new UserEditForm();
        userEditForm.setName(userAccount.getName());
        userEditForm.setEmail(userAccount.getEmail());

        model.addAttribute("userEditForm", userEditForm);
        return "edit_profile";
    }



    @PostMapping("/update_profile")
    public String updateProfile(@Validated(UserEditForm.ValidationGroups.PasswordCheck.class) @ModelAttribute("userEditForm") UserEditForm userEditForm,
                                BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("userForm", userEditForm);
            return "edit_profile";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserAccount existingUser = userAccountService.getUser(currentUsername);

        existingUser.setName(userEditForm.getName());
        existingUser.setEmail(userEditForm.getEmail());

        // Only update password if it's provided
        if (userEditForm.getPassword() != null && !userEditForm.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userEditForm.getPassword()));
        }

        //TODO: implement image storage here

        // Save the updated user
        userAccountService.updateUserProfile(currentUsername, userEditForm);

        return "redirect:/user_dashboard";
    }


}
