package com.sponsorando.app.services;

import com.sponsorando.app.models.*;
import com.sponsorando.app.repositories.CampaignRepository;
import com.sponsorando.app.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AddressService addressService;

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    @Transactional
    public Campaign createCampaign(CampaignForm campaignForm, String email) {

        UserAccount userAccount = userAccountService.getUser(email);
        Campaign campaign = new Campaign();
        campaign.setTitle(campaignForm.getTitle());
        campaign.setSlug(SlugUtil.generateSlug(campaignForm.getTitle(), true, 100));
        campaign.setDescription(campaignForm.getDescription());
        campaign.setStartDate(campaignForm.getStartDate());
        campaign.setEndDate(campaignForm.getEndDate());
        campaign.setGoalAmount(campaignForm.getGoalAmount());
        campaign.setCurrency(campaignForm.getCurrency());
        campaign.setCollectedAmount(0.0);
        List<CampaignCategory> categories = campaignForm.getCategories();
        campaign.setCategories(categories);
        campaign.setShowLocation(campaignForm.getShowLocation());

        if (userAccount != null) {
            campaign.setUserAccount(userAccount);
            Address address = addressService.createAddress(campaignForm);

            if (address != null) {
                campaign.setAddress(address);
                return campaignRepository.save(campaign);
            }
        }
        return null;
    }

    public Page<Campaign> getCampaigns(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return campaignRepository.findAll(pageable);
    }

    public Page<Campaign> getActiveCampaignsByTitleOrCategory(String searchQuery, String sortBy, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, getSortOrder(sortBy));
        return campaignRepository.findByStatusAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                CampaignStatus.ACTIVE, searchQuery, pageable, sortBy);
    }

    public Page<Campaign> getCampaignsByStatus(String sortBy, int pageNumber, int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE, pageable, sortBy);
    }

    private Sort getSortOrder(String sortBy) {
        return switch (sortBy) {
            case "mostUrgent", "default" -> Sort.unsorted();
            case "fewestDaysLeft" -> Sort.by(Sort.Direction.ASC, "endDate");
            case "newest" -> Sort.by(Sort.Direction.DESC, "startDate");
            case "lowestCostToComplete" -> Sort.unsorted();
            case "mostDonors" -> Sort.unsorted();
            default -> Sort.unsorted();
        };
    }

    public Page<Campaign> getCampaignsByUserEmail(String email, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return campaignRepository.findByUserAccountEmailAndStatusNot(email, CampaignStatus.INACTIVE, pageable);
    }

    public int getTotalPages(String email, String role, int pageSize) {

        long totalCampaigns;

        if ("ROLE_ADMIN".equals(role)) {
            totalCampaigns = campaignRepository.count();
        } else if ("ROLE_USER".equals(role)) {
            totalCampaigns = campaignRepository.countByUserAccountEmailAndStatusNot(email, CampaignStatus.INACTIVE);
        } else {
            totalCampaigns = 0;
        }
        return (int) Math.ceil((double) totalCampaigns / pageSize);
    }

    @Transactional(readOnly = true)
    public Campaign getCampaignById(Long id) {
        try {
            return campaignRepository.findById(id)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Campaign not found with id: " + id)
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving campaign with id: " + id, e);
        }
    }

    @Transactional(readOnly = true)
    public Campaign getCampaignBySlug(String slug) {
        try {
            return campaignRepository.findBySlug(slug)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Campaign not found with slug: " + slug)
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving campaign with slug: " + slug, e);
        }
    }

    @Transactional
    public boolean deleteCampaign(Long campaignId) {

        try {
            if (imageService.deleteByCampaignId(campaignId)) {

                Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));
                campaign.setStatus(CampaignStatus.INACTIVE);
                campaign.setUpdatedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCampaign(CampaignForm updatedCampaignDetails) {
        try {

            Optional<Campaign> existingCampaignOptional = campaignRepository.findById(updatedCampaignDetails.getCampaignId());

            if (existingCampaignOptional.isEmpty()) {
                return false;
            }

            Campaign existingCampaign = existingCampaignOptional.get();

            existingCampaign.setStartDate(updatedCampaignDetails.getStartDate());
            existingCampaign.setTitle(updatedCampaignDetails.getTitle());
            existingCampaign.setSlug(SlugUtil.generateSlug(updatedCampaignDetails.getTitle(), true, 100));
            existingCampaign.setDescription(updatedCampaignDetails.getDescription());
            existingCampaign.setShowLocation(updatedCampaignDetails.getShowLocation() != null ? updatedCampaignDetails.getShowLocation() : false);
            existingCampaign.setCurrency(updatedCampaignDetails.getCurrency());
            existingCampaign.setGoalAmount(updatedCampaignDetails.getGoalAmount());
            existingCampaign.setEndDate(updatedCampaignDetails.getEndDate());
            existingCampaign.setCategories(updatedCampaignDetails.getCategories());
            existingCampaign.setUpdatedAt(LocalDateTime.now());
            addressService.updateAddress(updatedCampaignDetails, existingCampaign);
            campaignRepository.save(existingCampaign);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Campaign updateCampaignCollectedAmount(Long id, Double netAmount) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found with id: " + id));

        Double currentAmount = campaign.getCollectedAmount();
        Double newAmount = (currentAmount != null ? currentAmount : 0) + netAmount;
        campaign.setCollectedAmount(newAmount);
        campaign.setUpdatedAt(LocalDateTime.now());

        return campaignRepository.save(campaign);
    }

    public boolean requestApprovalCampaign(Long campaignId, String userEmail) {
        Optional<Campaign> optionalCampaign = campaignRepository.findById(campaignId);

        if (optionalCampaign.isPresent()) {
            Campaign campaign = optionalCampaign.get();

            if (campaign.getUserAccount().getEmail().equals(userEmail) &&
                    campaign.getStatus() == CampaignStatus.DRAFT) {

                campaign.setStatus(CampaignStatus.PENDING);
                campaign.setUpdatedAt(LocalDateTime.now());
                campaignRepository.save(campaign);
                return true;
            }
        }

        return false;
    }

    @Transactional
    public boolean validateCampaign(Long campaignId, String action, String currentRole) throws IllegalAccessException {
        if (!"ROLE_ADMIN".equals(currentRole)) {
            throw new IllegalAccessException("Only admins can validate campaigns.");
        }

        Optional<Campaign> optionalCampaign = campaignRepository.findById(campaignId);

        if (optionalCampaign.isPresent()) {
            Campaign campaign = optionalCampaign.get();

            if (campaign.getStatus() == CampaignStatus.PENDING) {
                if ("approve".equals(action)) {
                    campaign.setStatus(CampaignStatus.ACTIVE);
                    campaign.setUpdatedAt(LocalDateTime.now());
                } else if ("decline".equals(action)) {
                    campaign.setStatus(CampaignStatus.DECLINED);
                    campaign.setUpdatedAt(LocalDateTime.now());
                } else {
                    return false;
                }

                campaignRepository.save(campaign);
                return true;
            }
        }

        return false;
    }

    public List<CampaignCardDTO> getFeaturedCampaigns() {
        try {

            log.info("In getFeaturedCampaigns");
            List<Campaign> campaigns = campaignRepository.findActiveCampaignsWithMostDonors(
                    CampaignStatus.ACTIVE,
                    PageRequest.of(0, 6));
            return campaigns.stream()
                    .map(this::convertToCampaignCardDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching featured campaigns", e);
            return Collections.emptyList();
        }
    }

    private CampaignCardDTO convertToCampaignCardDTO(Campaign campaign) {
        List<CampaignCategoryDTO> categoryDTOs = campaign.getCategories().stream()
                .map(category -> new CampaignCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());

        CurrencyDTO currencyDTO = new CurrencyDTO(
                campaign.getCurrency().getCode(),
                campaign.getCurrency().getSymbol()
        );

        AddressDTO addressDTO = new AddressDTO(
                campaign.getAddress().getStreet(),
                campaign.getAddress().getNumber(),
                campaign.getAddress().getCity(),
                campaign.getAddress().getCountry(),
                campaign.getAddress().getPostcode(),
                campaign.getAddress().getLatitude(),
                campaign.getAddress().getLongitude()
        );

        return new CampaignCardDTO(
                campaign.getSlug(),
                campaign.getTitle(),
                categoryDTOs,
                campaign.getDescription(),
                campaign.getCollectedAmount(),
                campaign.getGoalAmount(),
                currencyDTO,
                campaign.getShowLocation(),
                addressDTO
        );
    }
}
