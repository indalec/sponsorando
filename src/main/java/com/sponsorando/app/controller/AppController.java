package com.sponsorando.app.controller;

import com.sponsorando.app.models.*;
import com.sponsorando.app.services.CampaignService;
import com.sponsorando.app.services.UserAccountService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CampaignService campaignService;

    @GetMapping("/")
    public String index(Model model) {
        List<CampaignCardDTO> campaigns = campaignService.getFeaturedCampaigns();
        model.addAttribute("campaigns", campaigns);
        return "index";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String redirect,
            Model model
    ) {
        if ("add-campaign".equals(redirect)) {
            model.addAttribute("showInfoMessage", true);
            model.addAttribute("message", "Please login to start a campaign");
        } else if ("donate-now".equals(redirect)) {
            model.addAttribute("showInfoMessage", true);
            model.addAttribute("message", "Please login to make a donation");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String postSignUp(
            @ModelAttribute @Valid UserForm userForm,
            RedirectAttributes redirectAttributes,
            Model model
    ){
        try {
            UserAccount user = userAccountService.createUserAccount(userForm);

            if(user != null) {
                redirectAttributes.addFlashAttribute("isSignedUp", true);
            } else {
                redirectAttributes.addFlashAttribute("isSignedUp", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("isSignedUp", false);
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(
            HttpServletResponse response,
            Model model
    ) {
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        model.addAttribute("username", null);
        return "redirect:/";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/team")
    public String team() {
        return "team";
    }
}
