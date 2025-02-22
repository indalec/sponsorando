package com.sponsorando.app.services;

import com.sponsorando.app.repositories.DonationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sponsorando.app.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private static final Logger logger = LoggerFactory.getLogger(DonationService.class);

    private final DonationRepository donationRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    CampaignService campaignService;

    @Transactional(readOnly = true)
    public List<Donation> getAllDonations() {
        logger.info("Fetching all donations");
        try {
            List<Donation> donations = donationRepository.findAll();
            logger.debug("Retrieved {} donations", donations.size());
            return donations;
        } catch (Exception e) {
            logger.error("Error fetching donations", e);
            throw new RuntimeException("Failed to retrieve donations", e);
        }
    }

    @Transactional
    public Donation createDonation(DonationForm form, String email, Long campaignId) {
        logger.info("Creating donation for user: {} and campaign: {}", email, campaignId);

        UserAccount userAccount = userAccountService.getUser(email);
        Campaign campaign = campaignService.getCampaignById(campaignId);

        Donation donation = new Donation();
        donation.setAmount(form.getAmount());
        donation.setCurrency(form.getCurrency());
        donation.setDonationDate(LocalDateTime.now());
        donation.setCampaign(campaign);
        donation.setUserAccount(userAccount);
        donation.setAnonymous(form.isAnonymous());

        donation = donationRepository.save(donation);
        logger.info("Donation created successfully with ID: {}", donation.getId());

        return donation;
    }

    @Transactional
    public void deleteDonation(Long id) {
        logger.info("Attempting to delete donation with ID: {}", id);
        try {
            Optional<Donation> donationOptional = donationRepository.findById(id);
            if (donationOptional.isPresent()) {
                donationRepository.delete(donationOptional.get());
                logger.info("Donation with ID: {} successfully deleted", id);
            } else {
                logger.warn("Donation with ID: {} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting donation with ID: {}", id, e);
            throw new RuntimeException("Failed to delete donation", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Donation> getDonationById(Long id) {
        logger.info("Fetching donation with ID: {}", id);
        Optional<Donation> donation = donationRepository.findById(id);
        if (donation.isPresent()) {
            logger.info("Donation found: {}", donation.get());
        } else {
            logger.warn("No donation found with ID: {}", id);
        }
        return donation;
    }

    public Page<Donation> getDonations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return donationRepository.findAll(pageable);
    }

    public Page<Donation> getDonationsByUserEmail(String email, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return donationRepository.findByUserAccountEmail(email, pageable);
    }
}
