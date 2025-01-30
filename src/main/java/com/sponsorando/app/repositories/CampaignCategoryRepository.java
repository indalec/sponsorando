package com.sponsorando.app.repositories;

import com.sponsorando.app.models.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {


}
