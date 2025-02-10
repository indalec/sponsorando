package com.sponsorando.app.services;

import com.sponsorando.app.models.Address;
import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignForm;
import com.sponsorando.app.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddressService {


    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(CampaignForm campaignForm) {

        Address address = new Address();
        address.setStreet(campaignForm.getStreet());
        address.setNumber(campaignForm.getNumber());
        address.setCity(campaignForm.getCity());
        address.setCountry(campaignForm.getCountry());
        address.setPostcode(campaignForm.getPostcode());
        address.setLatitude(campaignForm.getLatitude());
        address.setLongitude(campaignForm.getLongitude());

        return addressRepository.save(address);

    }

    Address getAddress(CampaignForm updatedCampaignDetails, Campaign existingCampaign) {
        Address address = new Address();
        address.setId(existingCampaign.getAddress().getId());
        address.setStreet(updatedCampaignDetails.getStreet());
        address.setNumber(updatedCampaignDetails.getNumber());
        address.setCity(updatedCampaignDetails.getCity());
        address.setCountry(updatedCampaignDetails.getCountry());
        address.setPostcode(updatedCampaignDetails.getPostcode());
        address.setLatitude(updatedCampaignDetails.getLatitude());
        address.setLongitude(updatedCampaignDetails.getLongitude());
        address.setUpdatedAt(LocalDateTime.now());
        return address;
    }
}
