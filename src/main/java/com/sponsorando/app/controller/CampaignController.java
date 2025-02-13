package com.sponsorando.app.controller;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignCategoryRepository;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.repositories.CurrencyRepository;
import com.sponsorando.app.services.CampaignService;
import com.sponsorando.app.services.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class CampaignController {

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/discover_campaigns")
    public String discoverCampaigns(Model model,
                                    @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                    @RequestParam(name = "searchQuery", required = false) String searchQuery) {
        int pageSize = 5;
        Page<Campaign> page;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            page = campaignService.getActiveCampaignsByTitleOrCategory(searchQuery, pageNumber, pageSize);
        } else {
            page = campaignService.getCampaignsByStatus(pageNumber, pageSize);
        }

        model.addAttribute("campaigns", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("searchQuery", searchQuery);

        return "discover_campaigns";
    }

    @GetMapping("/add_campaign")
    @PreAuthorize("isAuthenticated()")
    public String addCampaign(Model model) {

        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        List<Currency> currencies = currencyRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("currencies", currencies);

        return "add_campaign";
    }

    @PostMapping("/add_campaign")
    public String addCampaign(
            @ModelAttribute @Valid CampaignForm campaignForm,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        String email = (String) model.getAttribute("username");

        if (email != null) {

            try {
                Campaign campaign = campaignService.createCampaign(campaignForm, email);
                if (campaign != null) {
                    redirectAttributes.addFlashAttribute("campaign", campaign);
                    redirectAttributes.addFlashAttribute("isCampaignAdded", true);
                } else {
                    redirectAttributes.addFlashAttribute("isCampaignAdded", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("isCampaignAdded", false);
            }
        }
        return "redirect:/add_campaign";
    }

    @GetMapping("/campaigns")
    public String getAllCampaigns(Model model, @RequestParam(name = "page", defaultValue = "0") int pageNumber) {

        String currentUser = (String) model.getAttribute("username");
        String currentRole = (String) model.getAttribute("currentRole");

        int pageSize = 5;
        Page<Campaign> page;

        if ("ROLE_ADMIN".equals(currentRole)) {
            page = campaignService.getCampaigns(pageNumber, pageSize);
        } else if ("ROLE_USER".equals(currentRole)) {
            page = campaignService.getCampaignsByUserEmail(currentUser, pageNumber, pageSize);
        } else {
            page = campaignService.getCampaigns(pageNumber, pageSize);
        }

        model.addAttribute("campaigns", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber());

        if (currentUser != null && currentRole != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentRole", currentRole);
        }
        return "campaigns";
    }

    @GetMapping("/delete_campaign/{id}")
    public String deleteCampaign(@PathVariable("id") Long id, @RequestParam("page") int currentPage, Model model, RedirectAttributes redirectAttributes) {

        boolean isCampaignDeleted = campaignService.deleteCampaign(id);
        String email = (String) model.getAttribute("username");
        String role = (String) model.getAttribute("currentRole");

        int totalPages = campaignService.getTotalPages(email, role, 5);

        if (totalPages > 0 && currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }

        redirectAttributes.addFlashAttribute("isCampaignDeleted", isCampaignDeleted);
        redirectAttributes.addFlashAttribute("currentRole", role);
        return "redirect:/campaigns?page=" + currentPage;
    }

    @GetMapping("/c/{slug}")
    public String viewCampaign(
            @PathVariable String slug,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String userEmail = (String) model.getAttribute("username");
            UserAccount currentUser = userAccountService.getUser(userEmail);
            if (currentUser != null) {
                model.addAttribute("currentUserId", currentUser.getId());
            } else {
                model.addAttribute("currentUserId", 0);
            }

            String currentRole = (String) model.getAttribute("currentRole");

            boolean isAdmin = currentRole != null && currentRole.contains("ROLE_ADMIN");
            boolean isUser = currentRole != null && currentRole.contains("ROLE_USER");
            boolean isGuest =  currentRole == null || currentRole.isEmpty() || currentRole.contains("ROLE_NONE");

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isUser", isUser);
            model.addAttribute("isGuest", isGuest);

            Campaign campaign = campaignService.getCampaignBySlug(slug);
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }

            model.addAttribute("page", page);

            return "view_campaign";
        } catch (Exception e) {
            e.printStackTrace();

            String currentRole = (String) model.getAttribute("currentRole");
            boolean isGuest =  currentRole == null || currentRole.isEmpty() || currentRole.contains("ROLE_NONE");

            if(isGuest) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unfortunately an error occurred while retrieving the campaign. Please try again.");
                return "redirect:/discover_campaigns?page=" + page;
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unfortunately an error occurred while retrieving the campaign. Please try again.");
                return "redirect:/campaigns?page=" + page;
            }
        }
    }


    @GetMapping("/edit_campaign/{id}")
    public String editCampaign(@PathVariable("id") Long id, @RequestParam("page") int currentPage, Model model) {
        System.out.println("checkkkkkkkkkkkkk");

        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        List<Currency> currencies = currencyRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("currencies", currencies);
        model.addAttribute("currentRole", (String) model.getAttribute("currentRole"));
        model.addAttribute("page", currentPage);

        Optional<Campaign> campaign = campaignRepository.findById(id);

        if (campaign.isPresent() && campaign.get().getStartDate() != null) {

            String formattedStartDate = campaign.get().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            model.addAttribute("formattedStartDate", formattedStartDate);
            model.addAttribute("campaign", campaign.get());
            System.out.println("formattedStartDate:::"+formattedStartDate);
        }
        if (campaign.isPresent() && campaign.get().getEndDate() != null) {

            String formattedEndDate = campaign.get().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            model.addAttribute("formattedEndDate", formattedEndDate);
            model.addAttribute("campaign", campaign.get());
            System.out.println("formattedEndDate:::"+formattedEndDate);
        }
        model.addAttribute("campaignStatuses", CampaignStatus.getCampaignStatuses());
        return "edit_campaign";
    }

    @PostMapping("/edit_campaign")
    public String editCampaignSubmit(@ModelAttribute @Valid CampaignForm campaignForm,
                                     RedirectAttributes redirectAttributes, Model model, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "edit_campaign";
        }
        System.out.println(campaignForm);

        String role = (String) model.getAttribute("currentRole");

        boolean isCampaignUpdated = false;

        try {
            isCampaignUpdated = campaignService.updateCampaign(campaignForm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isCampaignUpdated) {
            redirectAttributes.addFlashAttribute("isCampaignUpdated", true);
        } else {
            redirectAttributes.addFlashAttribute("isCampaignUpdated", false);
        }

        redirectAttributes.addFlashAttribute("currentRole", role);
        return "redirect:/campaigns?page=" + campaignForm.getPage();
    }

}
