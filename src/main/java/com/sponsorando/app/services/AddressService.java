package com.sponsorando.app.services;

import com.sponsorando.app.models.Address;
import com.sponsorando.app.models.Campaign;
import com.sponsorando.app.models.CampaignForm;
import com.sponsorando.app.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;


    public Address createAddress(CampaignForm campaignForm) {

        List<Address> existingAddresses = addressRepository.findByStreetAndNumberAndCityAndPostcodeAndCountryAndLatitudeAndLongitude(
                campaignForm.getStreet(),
                campaignForm.getNumber(),
                campaignForm.getCity(),
                campaignForm.getPostcode(),
                campaignForm.getCountry(),
                campaignForm.getLatitude(),
                campaignForm.getLongitude()
        );

        if (!existingAddresses.isEmpty()) {

            return existingAddresses.get(0);
        }

        Address address = new Address();
        address.setStreet(campaignForm.getStreet());
        address.setNumber(campaignForm.getNumber());
        address.setCity(campaignForm.getCity());
        address.setCountry(campaignForm.getCountry());
        address.setPostcode(campaignForm.getPostcode());
        address.setLatitude(campaignForm.getLatitude());
        address.setLongitude(campaignForm.getLatitude());

        return addressRepository.save(address);
    }



    public Address updateAddress(CampaignForm updatedCampaignDetails, Campaign existingCampaign) {
        Address address = new Address();
        address.setId(existingCampaign.getAddress().getId());
        address.setStreet(updatedCampaignDetails.getStreet());
        address.setNumber(updatedCampaignDetails.getNumber());
        address.setCity(updatedCampaignDetails.getCity());
        address.setCountry(updatedCampaignDetails.getCountry());
        address.setPostcode(updatedCampaignDetails.getPostcode());
        address.setLatitude(updatedCampaignDetails.getLatitude());
        address.setLongitude(updatedCampaignDetails.getLongitude());
        address.setCreatedAt(existingCampaign.getAddress().getCreatedAt());
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }
}
