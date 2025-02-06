package com.sponsorando.app.controller;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignCategory;
import com.sponsorando.app.models.CampaignForm;
import com.sponsorando.app.models.Currency;
import com.sponsorando.app.repositories.CampaignCategoryRepository;
import com.sponsorando.app.repositories.CurrencyRepository;
import com.sponsorando.app.services.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CampaignController {

    @Autowired
    private CampaignCategoryRepository campaignCategoryRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/discover_campaigns")
    public String discoverCampaigns(Model model) {

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
}
