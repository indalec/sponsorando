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

    public Address findOrCreateAddress(Address address) {
        // Try to find an existing address
        Address existingAddress = addressRepository.findByStreetAndNumberAndCityAndPostcodeAndCountryAndLatitudeAndLongitude(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getPostcode(),
                address.getCountry(),
                address.getLatitude(),
                address.getLongitude()
        );

        if (existingAddress != null) {
            return existingAddress;
        }


        return addressRepository.save(address);
    }



    public Address createAddress(CampaignForm campaignForm) {

        // Check if an existing address matches the form data, including latitude and longitude
        Address existingAddress = addressRepository.findByStreetAndNumberAndCityAndPostcodeAndCountryAndLatitudeAndLongitude(
                campaignForm.getStreet(),
                campaignForm.getNumber(),
                campaignForm.getCity(),
                campaignForm.getPostcode(),
                campaignForm.getCountry(),
                Double.parseDouble(campaignForm.getLatitude()), //IMPORTANT: parsing and passing Double values instead of String
                Double.parseDouble(campaignForm.getLongitude())  //IMPORTANT: parsing and passing Double values instead of String
        );

        if (existingAddress != null) {
            return existingAddress; // Return the existing address if found
        }

        // If no matching address was found, create a new one
        Address address = new Address();
        address.setStreet(campaignForm.getStreet());
        address.setNumber(campaignForm.getNumber());
        address.setCity(campaignForm.getCity());
        address.setCountry(campaignForm.getCountry());
        address.setPostcode(campaignForm.getPostcode());
        address.setLatitude(Double.parseDouble(campaignForm.getLatitude())); //IMPORTANT: parsing and passing Double values instead of String
        address.setLongitude(Double.parseDouble(campaignForm.getLongitude()));  //IMPORTANT: parsing and passing Double values instead of String

        return addressRepository.save(address); // Save the newly created address
    }
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
