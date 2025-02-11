package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long > {

    Page<Campaign> findByUserAccountEmailAndStatusNot(String email, CampaignStatus status, Pageable pageable);
    long countByUserAccountEmailAndStatusNot(String email, CampaignStatus status);
    Optional<Campaign> findBySlug(String slug);

}
