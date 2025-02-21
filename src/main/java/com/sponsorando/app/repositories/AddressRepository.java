package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByStreetAndNumberAndCityAndPostcodeAndCountryAndLatitudeAndLongitude(String street,
                                                                                     String number,
                                                                                     String city,
                                                                                     String postcode,
                                                                                     String country,
                                                                                     Double latitude,
                                                                                     Double longitude);
}
