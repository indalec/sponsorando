package com.sponsorando.app.models;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign_categories")
public class CampaignCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
