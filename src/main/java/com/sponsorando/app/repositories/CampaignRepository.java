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
public interface CampaignRepository extends JpaRepository<Campaign, Long > {

    Page<Campaign> findByUserAccountEmailAndStatusNot(String email, CampaignStatus status, Pageable pageable);
    long countByUserAccountEmailAndStatusNot(String email, CampaignStatus status);
    Optional<Campaign> findBySlug(String slug);
    @Query("SELECT c FROM Campaign c " +
            "JOIN c.categories cat " +
            "WHERE c.status = :status " +
            "AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "OR LOWER(cat.name) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Campaign> findByStatusAndTitleContainingIgnoreCaseOrStatusAndCategoryContainingIgnoreCase(
            @Param("status") CampaignStatus status,
            @Param("title") String title,
            @Param("category") String category,
            Pageable pageable);


    Page<Campaign> findByStatus(CampaignStatus status, Pageable pageable);


}
