package com.sponsorando.app.controller;

import com.sponsorando.app.models.*;
import com.sponsorando.app.services.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class DonationController {

    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/donations")
    @PreAuthorize("hasRole('ADMIN')")
    public String donations(Model model) {
        List<Donation> donations = donationService.getAllDonations();
        model.addAttribute("donations", donations);
        return "donations";
    }

    @GetMapping("/donate-now/{id}")
    public String donateNow(@PathVariable("id") Long campaignId, Model model, RedirectAttributes redirectAttributes) {
        String currentRole = (String) model.getAttribute("currentRole");

        boolean isAdmin = currentRole != null && currentRole.contains("ROLE_ADMIN");
        boolean isUser = currentRole != null && currentRole.contains("ROLE_USER");
        boolean isGuest = currentRole == null || currentRole.isEmpty() || currentRole.contains("ROLE_NONE");

        if (isGuest) {
            redirectAttributes.addFlashAttribute("isGuest", true);
            redirectAttributes.addFlashAttribute("showInfoMessage", true);
            redirectAttributes.addFlashAttribute("message", "Please first login to donate");
            return "redirect:/login";
        }

        if (isAdmin) {
            redirectAttributes.addFlashAttribute("isAdmin", true);
            redirectAttributes.addFlashAttribute("showInfoMessage", true);
            redirectAttributes.addFlashAttribute("message", "Please login as user to donate");
            return "redirect:/login";
        }

        if (isUser) {
            String email = (String) model.getAttribute("username");
            UserAccount currentUser = userAccountService.getUser(email);
            List<Currency> currencies = currencyService.getCurrencies();

            model.addAttribute("isUser", true);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currencies", currencies);
            model.addAttribute("campaignId", campaignId);
        }

        return "donate_now";
    }

    @PostMapping("/donate-now/{id}")
    @ResponseBody
    public ResponseEntity<?> postDonateNow(
            @PathVariable("id") Long campaignId,
            @Valid @ModelAttribute DonationForm donationForm,
            BindingResult bindingResult,
            @RequestParam("stripeToken") String stripeToken,
            Model model
    ) {
        logger.info("Received donation form submission for campaign ID: {}", campaignId);

        if (bindingResult.hasErrors()) {
            logger.warn("Form validation errors: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Please correct the form errors",
                    "errors", bindingResult.getAllErrors()
            ));
        }

        try {
            String email = (String) model.getAttribute("username");
            logger.info("Processing donation for user: {}", email);

            Donation donation = donationService.createDonation(donationForm, email, campaignId);
            Payment payment = paymentService.processPayment("STRIPE", stripeToken, donation);
            Campaign campaign = campaignService.getCampaignById(campaignId);

            if (payment.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
                logger.info("Payment successful for donation ID: {}", donation.getId());
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Thank you for your donation!",
                        "redirectUrl", "/c/" + campaign.getSlug()
                ));
            } else {
                logger.warn("Payment failed: {}", payment.getFailureMessage());
                donationService.deleteDonation(donation.getId());
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Payment failed: " + payment.getFailureMessage()
                ));
            }
        } catch (Exception e) {
            logger.error("Error processing donation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "An error occurred while processing your donation. Please try again."
            ));
        }
    }
}