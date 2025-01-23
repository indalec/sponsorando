package com.sponsorando.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @ModelAttribute("username")
    public String getUsername(Principal principal) {
        if (principal != null) {
            return principal.getName();
        }
        return null;
    }

    @ModelAttribute("roles")
    public Collection<? extends GrantedAuthority> getRoles(Authentication authentication ) {

        if(authentication != null) {
            return authentication.getAuthorities();
        }
        return Collections.emptyList();
    }


    @GetMapping("")
    public String admin() {
        return "admin";
    }

}
