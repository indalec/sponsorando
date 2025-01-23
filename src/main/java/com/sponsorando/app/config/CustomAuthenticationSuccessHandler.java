package com.sponsorando.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{

        // TODO remove print
        System.out.println("auth::::::::::::::::::::" + authentication.getAuthorities().toString());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        // TODO remove print
        System.out.println(isAdmin + "hey admin................................");
        String targetUrl = isAdmin ? "/admin" : "/";

        response.sendRedirect(targetUrl);


    }
}
