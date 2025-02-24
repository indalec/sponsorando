package com.sponsorando.app.controller;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignCategoryRepository;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.services.CampaignService;
import com.sponsorando.app.services.CurrencyService;
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
    private CurrencyService currencyService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping("/discover-campaigns")
    public String discoverCampaigns(Model model,
                                    @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                    @RequestParam(name = "searchQuery", required = false) String searchQuery,
                                    @RequestParam(name = "sortBy", defaultValue = "mostUrgent") String sortBy
    ) {

        int pageSize = 9;
        Page<Campaign> page;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            page = campaignService.getActiveCampaignsByTitleOrCategory(searchQuery, sortBy, pageNumber, pageSize);
        } else {
            page = campaignService.getCampaignsByStatus(sortBy, pageNumber, pageSize);
        }

        model.addAttribute("campaigns", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortBy", sortBy);

        return "discover_campaigns";
    }

    @GetMapping("/add-campaign")
    @PreAuthorize("isAuthenticated()")
    public String addCampaign(Model model) {

        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        List<Currency> currencies = currencyService.getCurrencies();

        model.addAttribute("categories", categories);
        model.addAttribute("currencies", currencies);

        return "add_campaign";
    }

    @PostMapping("/add-campaign")
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
        return "redirect:/add-campaign";
    }

    @GetMapping("/campaigns")
    public String getAllCampaigns(Model model, @RequestParam(name = "page", defaultValue = "0") int pageNumber) {

        String currentUser = (String) model.getAttribute("username");
        String currentRole = (String) model.getAttribute("currentRole");

        int pageSize = 10;
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
            @RequestParam(value = "source", required = false) String source,
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
            model.addAttribute("source", source);

            return "view_campaign";
        } catch (Exception e) {
            e.printStackTrace();

            String currentRole = (String) model.getAttribute("currentRole");
            boolean isGuest =  currentRole == null || currentRole.isEmpty() || currentRole.contains("ROLE_NONE");

            if(isGuest) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unfortunately an error occurred while retrieving the campaign. Please try again.");
                return "redirect:/discover-campaigns?page=" + page;
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unfortunately an error occurred while retrieving the campaign. Please try again.");
                return "redirect:/campaigns?page=" + page;
            }
        }
    }


    @GetMapping("/edit_campaign/{id}")
    public String editCampaign(@PathVariable("id") Long id, @RequestParam("page") int currentPage, Model model) {

        List<CampaignCategory> categories = campaignCategoryRepository.findAll();
        List<Currency> currencies = currencyService.getCurrencies();

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

    @GetMapping("/request_approval_campaign/{id}")
    public String requestApprovalCampaign(
            @PathVariable("id") Long campaignId,
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        String currentUserEmail = (String) model.getAttribute("username");

        try {
            boolean isUpdated = campaignService.requestApprovalCampaign(campaignId, currentUserEmail);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute("successMessage", "Approval requested successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to request approval. Please try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while processing the request.");
        }

        return "redirect:/campaigns?page=" + currentPage;
    }

    @GetMapping("/validate_campaign/{id}")
    public String validateCampaign(
            @PathVariable("id") Long campaignId,
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "action", required = true) String action,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("currentRole") String currentRole
    ) {
        try {
            boolean isUpdated = campaignService.validateCampaign(campaignId, action, currentRole);
            if (isUpdated) {
                if ("approve".equals(action)) {
                    redirectAttributes.addFlashAttribute("successMessage", "Campaign approved successfully!");
                } else if ("decline".equals(action)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Campaign declined. Please review and update your campaign and try again.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid action specified.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update campaign status.");
            }
        } catch (IllegalAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to perform this action.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while processing the request.");
        }

        return "redirect:/campaigns?page=" + currentPage;
    }
}
