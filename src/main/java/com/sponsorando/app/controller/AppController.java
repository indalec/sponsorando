package com.sponsorando.app.controller;

import com.sponsorando.app.models.UserForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AppController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
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
        System.out.println("SIGNUP USER: " + userForm);
        redirectAttributes.addFlashAttribute("isSignedUp", true);
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
}
