package com.sponsorando.app.services;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ImageService imageService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserAccount userAccount = userAccountRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(email)
                );

        if (!userAccount.getEnabled()) {
            throw new DisabledException("This account has been disabled. Please contact an administrator.");
        }

        String role = userAccount.getRole().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        UserDetails userDetails =  User
                .withUsername(userAccount.getEmail())
                .password(userAccount.getPassword())
                .disabled(!userAccount.getEnabled())
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

    public void updateUserProfile(String currentEmail, UserEditForm form) {
        Optional<UserAccount> optionalUser = userAccountRepository.findByEmail(currentEmail);

        if (optionalUser.isPresent()) {
            UserAccount existingUser = optionalUser.get();

            if (form.getName() != null && !form.getName().isEmpty() &&
                    !form.getName().equals(existingUser.getName())) {
                existingUser.setName(form.getName());
            }

            if (form.getEmail() != null && !form.getEmail().isEmpty() &&
                    !form.getEmail().equals(existingUser.getEmail())) {
                existingUser.setEmail(form.getEmail());
            }

            if (form.getPassword() != null && !form.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(form.getPassword()));
            }

            // TODO: Implement image storage logic when ready


            userAccountRepository.save(existingUser);

            UserDetails userDetails = loadUserByUsername(existingUser.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    public List<UserAccount> getAllUsers(){
        return userAccountRepository.findAll();
    }

    public UserAccount getUserById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        UserAccount user = getUserById(id);
        user.setEnabled(!user.getEnabled());
        userAccountRepository.save(user);

        // Update related campaigns based on the new user status
        // TODO: status FROZEN ist just for testing purpose. Change logic if project gets to production phase.
        CampaignStatus newStatus = user.getEnabled() ? CampaignStatus.ACTIVE : CampaignStatus.FROZEN;
        updateCampaignStatus(user, newStatus);
    }

    private void updateCampaignStatus(UserAccount userAccount, CampaignStatus status) {
        List<Campaign> campaigns = campaignRepository.findByUserAccount(userAccount);
        System.out.println(campaigns);
        for (Campaign campaign : campaigns) {
            campaign.setStatus(status);
            campaign.setUpdatedAt(LocalDateTime.now());
            System.out.println(campaign.getTitle() + " was set to status: " + campaign.getStatus());
            campaignRepository.save(campaign);
        }
    }

    public void deleteUser(Long id) {
        UserAccount user = getUserById(id);
        user.setEnabled(false);
        userAccountRepository.save(user);
    }

    public void changeUserRole(Long id, String role) {
        UserAccount user = getUserById(id);
        if ("ADMIN".equalsIgnoreCase(role)) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        userAccountRepository.save(user);
    }


}
