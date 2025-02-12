package com.sponsorando.app.config;

import com.sponsorando.app.services.UserAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/campaigns").authenticated()
                            .requestMatchers("/add_campaign").authenticated()
                            .requestMatchers("/delete_campaign/**").authenticated()
                            .requestMatchers("/edit_campaign/**").authenticated()
                            .requestMatchers("/edit_campaign").authenticated()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/c/**").permitAll()
                            .requestMatchers("/discover_campaigns/**").permitAll()
                            .anyRequest().permitAll();
                }
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .authenticationEntryPoint(new AuthenticationEntryPoint() {
                            @Override
                            public void commence(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationException authException) throws IOException, ServletException {
                                String requestURI = request.getRequestURI();
                                if (requestURI.contains("/add_campaign")) {
                                    response.sendRedirect("/login?redirect=add_campaign");
                                } else {
                                    response.sendRedirect("/login");
                                }
                            }
                        })
        );

        http.csrf(csrfConfigurer -> {
            csrfConfigurer.ignoringRequestMatchers("/api/**");
        });

//        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userAccountService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}
