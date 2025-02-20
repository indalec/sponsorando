package com.sponsorando.app.controller;

import com.sponsorando.app.models.CampaignStatus;
import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.models.UserEditForm;
import com.sponsorando.app.models.UserPanelDetailsDto;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UsersController {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CampaignRepository campaignRepository;

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "id") String sort,
                        @RequestParam(defaultValue = "asc") String order,
                        Model model) {
        try {
            List<UserAccount> users = userAccountService.getAllUsers(sort, order);
            model.addAttribute("users", users);
            model.addAttribute("sortField", sort);
            model.addAttribute("sortDir", order);
        } catch (IllegalArgumentException ex) {
            System.out.println("Ocurred the following error" + ex);
            List<UserAccount> users = userAccountService.getAllUsers("id", "asc");
            model.addAttribute("users", users);
            model.addAttribute("sortField", "id");
            model.addAttribute("sortDir", "asc");
        }

        return "users";
    }



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
    public String updateProfile(@Validated({UserEditForm.ValidationGroups.PasswordCheck.class,
                                        UserEditForm.ValidationGroups.NameCheck.class,
                                        UserEditForm.ValidationGroups.EmailCheck.class})
                                @ModelAttribute("userEditForm") UserEditForm userEditForm,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("userEditForm", userEditForm);
            return "edit_profile";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        UserAccount existingUser = userAccountService.getUser(currentUsername);

        existingUser.setName(userEditForm.getName());
        existingUser.setEmail(userEditForm.getEmail());

        if (userEditForm.getPassword() != null && !userEditForm.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userEditForm.getPassword()));
        }

        //TODO: implement image storage here

        userAccountService.updateUserProfile(currentUsername, userEditForm);

        return "redirect:/user_dashboard";
    }

//    @GetMapping("/users/api")
//    public ResponseEntity<List<UserAccount>> getAllUsers(){
//        return ResponseEntity.ok(userAccountService.getAllUsers());
//    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserPanelDetailsDto> getUserById(@PathVariable Long id) {
        UserAccount user = userAccountService.getUserById(id);
        UserPanelDetailsDto dto = new UserPanelDetailsDto();

        dto.setId(user.getId());
        dto.setUsername(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());

        // Fetch campaign statistics
        int totalCampaigns = campaignRepository.countByUserAccount(user);
        dto.setTotalCampaigns(totalCampaigns);
        int draftCampaigns = campaignRepository.countByUserAccountAndStatus(user, CampaignStatus.DRAFT); // Assuming you have a DRAFT status
        dto.setDraftCampaigns(draftCampaigns);
        int activeCampaigns = campaignRepository.countByUserAccountAndStatus(user, CampaignStatus.ACTIVE);
        dto.setActiveCampaigns(activeCampaigns);
        int inactiveCampaigns = campaignRepository.countByUserAccountAndStatus(user, CampaignStatus.INACTIVE);
        dto.setInactiveCampaigns(inactiveCampaigns);
        int frozenCampaigns = campaignRepository.countByUserAccountAndStatus(user, CampaignStatus.FROZEN); // Assuming you have a FROZEN status
        dto.setFrozenCampaigns(frozenCampaigns);
        int completedCampaigns = campaignRepository.countByUserAccountAndStatus(user, CampaignStatus.COMPLETED); // Assuming you have a COMPLETED status
        dto.setCompletedCampaigns(completedCampaigns);

        // Placeholder values for donations
        dto.setTotalDonationsCollectedInEuro(0.0);
        dto.setTotalDonationsGivenInEuro(0.0);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam(name = "userId", required = true) Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        userAccountService.toggleUserStatus(id);
        return "redirect:/users";
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }


}
