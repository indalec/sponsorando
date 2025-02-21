package com.sponsorando.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String redirectUrl = "/login";

        Throwable cause = exception;
        if (exception instanceof InternalAuthenticationServiceException) {
            cause = exception.getCause();
        }

        if (cause instanceof DisabledException) {
            redirectUrl += "?disabled=true";
        } else {
            redirectUrl += "?error=true";
        }

        response.sendRedirect(redirectUrl);
    }
}
