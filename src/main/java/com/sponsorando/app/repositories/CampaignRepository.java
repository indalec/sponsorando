package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long > {
    List<Donation> getDonations(Long campaignId);
}
