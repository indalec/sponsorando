package com.sponsorando.app.advice;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@ControllerAdvice
public class GlobalModelAttributes {

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

    @ModelAttribute("currentRole")
    public String getCurrentRole(Authentication authentication) {
        if (authentication != null && !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

}
