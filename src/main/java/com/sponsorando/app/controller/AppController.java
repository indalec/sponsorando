package com.sponsorando.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@Controller
public class AppController {

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

   @GetMapping("/")
    public String index() {
       return "index";
   }

   @GetMapping("/login")
    public String login() {
        return "login";
   }


}
