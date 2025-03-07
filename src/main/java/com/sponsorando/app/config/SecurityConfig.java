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

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/campaigns").authenticated()
                            .requestMatchers("/add-campaign").authenticated()
                            .requestMatchers("/delete_campaign/**").authenticated()
                            .requestMatchers("/edit_campaign/**").authenticated()
                            .requestMatchers("/edit_campaign").authenticated()
                            .requestMatchers("/update_profile").authenticated()
                            .requestMatchers("/donations").authenticated()
                            .requestMatchers("/d/**").authenticated()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/donations/**").hasRole("ADMIN")
                            .requestMatchers("/payments/**").hasRole("ADMIN")
                            .requestMatchers("/users/**").hasRole("ADMIN")
                            .requestMatchers("/api/stripe-public-key").hasRole("USER")
                            .anyRequest().permitAll();
                }
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
        );

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .authenticationEntryPoint(new AuthenticationEntryPoint() {
                            @Override
                            public void commence(HttpServletRequest request, HttpServletResponse response,
                                                 AuthenticationException authException) throws IOException, ServletException {
                                String requestURI = request.getRequestURI();
                                if (requestURI.contains("/add-campaign")) {
                                    response.sendRedirect("/login?redirect=add-campaign");
                                } else if (requestURI.contains("/donate-now")) {
                                    response.sendRedirect("/login?redirect=donate-now");
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
