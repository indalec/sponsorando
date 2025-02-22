package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignStatus;
import com.sponsorando.app.models.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Page<Donation> findByUserAccountEmail(String email, Pageable pageable);
}
