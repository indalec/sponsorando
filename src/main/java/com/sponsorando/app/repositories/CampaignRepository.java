package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findByUserAccountEmailAndStatusNot(String email, CampaignStatus status, Pageable pageable);

    long countByUserAccountEmailAndStatusNot(String email, CampaignStatus status);

    Optional<Campaign> findBySlug(String slug);

    @Query("SELECT c FROM Campaign c WHERE c.id IN (" +
            "SELECT DISTINCT c2.id FROM Campaign c2 " +
            "LEFT JOIN c2.categories cat " +
            "WHERE c2.status = :status " +
            "AND (LOWER(c2.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')))) " +
            "ORDER BY " +
            "   CASE " +
            "     WHEN :sortBy = 'mostDonors' THEN (SELECT COUNT(DISTINCT d.userAccount) FROM Donation d WHERE d.campaign = c) " +
            "     WHEN :sortBy = 'newest' THEN c.startDate " +
            "     WHEN :sortBy = 'lowestCostToComplete' THEN (c.goalAmount - c.collectedAmount) " +
            "     ELSE 0 " +
            "   END DESC, " +
            "   CASE " +
            "     WHEN :sortBy IN ('mostUrgent', 'default', 'fewestDaysLeft') THEN c.endDate " +
            "     ELSE NULL " +
            "   END ASC, " +
            "   CASE " +
            "     WHEN :sortBy IN ('mostUrgent', 'default') THEN (c.goalAmount - c.collectedAmount) " +
            "     ELSE NULL " +
            "   END ASC")
    Page<Campaign> findByStatusAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            @Param("status") CampaignStatus status,
            @Param("searchQuery") String searchQuery,
            Pageable pageable,
            @Param("sortBy") String sortBy);



    @Query("SELECT c FROM Campaign c " +
            "LEFT JOIN (SELECT d.campaign.id as campaignId, COUNT(DISTINCT d.userAccount) as donorCount " +
            "           FROM Donation d GROUP BY d.campaign.id) as donorCounts " +
            "ON c.id = donorCounts.campaignId " +
            "WHERE c.status = :status " +
            "ORDER BY " +
            "   CASE " +
            "     WHEN :sortBy = 'mostDonors' THEN COALESCE(donorCounts.donorCount, 0) " +
            "     WHEN :sortBy = 'newest' THEN c.startDate " +
            "     WHEN :sortBy = 'lowestCostToComplete' THEN (c.goalAmount - c.collectedAmount) " +
            "     ELSE 0 " +
            "   END DESC, " +
            "   CASE " +
            "     WHEN :sortBy IN ('mostUrgent', 'default', 'fewestDaysLeft') THEN c.endDate " +
            "     ELSE NULL " +
            "   END ASC, " +
            "   CASE " +
            "     WHEN :sortBy IN ('mostUrgent', 'default') THEN (c.goalAmount - c.collectedAmount) " +
            "     ELSE NULL " +
            "   END ASC")
    Page<Campaign> findByStatus(
            @Param("status") CampaignStatus status,
            Pageable pageable,
            @Param("sortBy") String sortBy);

    @Query("SELECT c, COUNT(DISTINCT d.userAccount) as donorCount " +
            "FROM Campaign c " +
            "LEFT JOIN Donation d ON d.campaign = c " +
            "LEFT JOIN Payment p ON p.donation = d " +
            "WHERE c.status = :status AND p.paymentStatus = com.sponsorando.app.models.PaymentStatus.SUCCEEDED " +
            "GROUP BY c " +
            "ORDER BY donorCount DESC")
    List<Campaign> findActiveCampaignsWithMostDonors(@Param("status") CampaignStatus status, Pageable pageable);



}
