package com.sponsorando.app.services;

import com.sponsorando.app.models.ImageForm;
import com.sponsorando.app.models.Role;
import com.sponsorando.app.models.UserAccount;
import com.sponsorando.app.models.UserForm;
import com.sponsorando.app.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserAccountService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ImageService imageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserAccount userAccount = userAccountRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(email)
                );

        String role = userAccount.getRole().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        UserDetails userDetails =  User
                .withUsername(userAccount.getEmail())
                .password(userAccount.getPassword())
                .authorities(authority)
                .build();

        return userDetails;
    }

    public UserAccount createUserAccount(UserForm form) {
        UserAccount userAccount = new UserAccount();

        userAccount.setName(form.getName());
        userAccount.setEmail(form.getEmail());
        userAccount.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
        userAccount.setRole(Role.USER);
        if (form.getImageUrl() != null) {
            ImageForm imageForm = new ImageForm();
            imageForm.setUrl(form.getImageUrl());
            imageForm.setAltText(form.getName() + "'s profile picture");
            userAccount.setImageUrl(imageService.createImage(imageForm));
        }
        userAccount.setEnabled(true);

        userAccountRepository.save(userAccount);
        return userAccount;
    }

    public UserAccount getUser(String email) {
        Optional<UserAccount> user = userAccountRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    public void updateUserProfile(UserAccount updatedUser) {
        Optional<UserAccount> existingUserOptional = userAccountRepository.findByEmail(updatedUser.getEmail());

        if (existingUserOptional.isPresent()) {
            UserAccount existingUser = existingUserOptional.get();

            if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()
                    && !updatedUser.getName().equals(existingUser.getName())) {
                existingUser.setName(updatedUser.getName());
            }

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            if (updatedUser.getImageUrl() != null && !updatedUser.getImageUrl().isEmpty()
                    && !updatedUser.getImageUrl().equals(existingUser.getImageUrl())) {
                existingUser.setImageUrl(updatedUser.getImageUrl());
            }

            userAccountRepository.save(existingUser);
        }
    }




}
