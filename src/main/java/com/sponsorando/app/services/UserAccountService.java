package com.sponsorando.app.services;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<UserAccount> getAllUsers(String sortField, String sortDir, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userAccountRepository.findAll(pageable);
    }

    public UserAccount getUserById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        UserAccount user = getUserById(id);
        boolean enabled = !user.getEnabled();
        user.setEnabled(enabled);
        user.setUpdatedAt(LocalDateTime.now());
        userAccountRepository.save(user);

        // Update related campaigns based on the new user status
        // TODO: status FROZEN is just for testing purpose. Change logic if project gets to production phase.
        updateCampaignStatusFromOneUserWhenAccountDisabled(user, enabled);
    }

    private void updateCampaignStatusFromOneUserWhenAccountDisabled(UserAccount userAccount, boolean enabled) {
        List<Campaign> campaigns = campaignRepository.findByUserAccount(userAccount);
        System.out.println("Campaigns found for user: " + campaigns);

        for (Campaign campaign : campaigns) {
            if (enabled) {

                if (campaign.getStatus() == CampaignStatus.FROZEN) {
                    campaign.setStatus(CampaignStatus.ACTIVE);
                    campaign.setUpdatedAt(LocalDateTime.now());
                    System.out.println("Campaign '" + campaign.getTitle() + "' set to ACTIVE");
                    campaignRepository.save(campaign);
                }
            } else {

                if (campaign.getStatus() == CampaignStatus.ACTIVE) {
                    campaign.setStatus(CampaignStatus.FROZEN);
                    campaign.setUpdatedAt(LocalDateTime.now());
                    System.out.println("Campaign '" + campaign.getTitle() + "' set to FROZEN");
                    campaignRepository.save(campaign);
                }
            }
        }
    }


    public Page<UserAccount> searchUsers(String search, String sortField, String sortDir, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userAccountRepository.searchUsers(search.toLowerCase(), pageable);
    }



}
