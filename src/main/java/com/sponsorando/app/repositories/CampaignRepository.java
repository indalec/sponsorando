package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findByUserAccountEmailAndStatusNot(String email, CampaignStatus status, Pageable pageable);

    long countByUserAccountEmailAndStatusNot(String email, CampaignStatus status);

    Optional<Campaign> findBySlug(String slug);

    @Query("SELECT DISTINCT c FROM Campaign c " +
            "LEFT JOIN c.categories cat " +
            "LEFT JOIN c.images img " +
            "WHERE c.status = :status " +
            "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
            "GROUP BY c " +
            "ORDER BY " +
            "   CASE " +
            "     WHEN :sortBy = 'mostDonors' THEN (SELECT COUNT(DISTINCT d.userAccount) FROM Donation d WHERE d.campaign = c) " +
            "     WHEN :sortBy IN ('mostUrgent', 'default') THEN c.endDate " +
            "     WHEN :sortBy = 'fewestDaysLeft' THEN c.endDate " +
            "     WHEN :sortBy = 'newest' THEN c.startDate " +
            "     WHEN :sortBy = 'lowestCostToComplete' THEN (c.goalAmount - c.collectedAmount) " +
            "   END ASC, " +
            "   CASE WHEN :sortBy IN ('mostUrgent', 'default') THEN (c.goalAmount - c.collectedAmount) END DESC")
    Page<Campaign> findByStatusAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            @Param("status") CampaignStatus status,
            @Param("searchQuery") String searchQuery,
            Pageable pageable,
            @Param("sortBy") String sortBy);

    @Query("SELECT c FROM Campaign c " +
            "WHERE c.status = :status " +
            "ORDER BY " +
            "   CASE " +
            "     WHEN :sortBy = 'mostDonors' THEN (SELECT COUNT(DISTINCT d.userAccount) FROM Donation d WHERE d.campaign = c) " +
            "     WHEN :sortBy IN ('mostUrgent', 'default') THEN c.endDate " +
            "     WHEN :sortBy = 'fewestDaysLeft' THEN c.endDate " +
            "     WHEN :sortBy = 'newest' THEN c.startDate " +
            "     WHEN :sortBy = 'lowestCostToComplete' THEN (c.goalAmount - c.collectedAmount) " +
            "   END ASC, " +
            "   CASE WHEN :sortBy IN ('mostUrgent', 'default') THEN (c.goalAmount - c.collectedAmount) END DESC")
    Page<Campaign> findByStatus(
            @Param("status") CampaignStatus status,
            Pageable pageable,
            @Param("sortBy") String sortBy);

}
