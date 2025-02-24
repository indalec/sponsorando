package com.sponsorando.app.repositories;

import com.sponsorando.app.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {



    List<Address> findByStreetAndNumberAndCityAndPostcodeAndCountry(String street,
                                                                    String number,
                                                                    String city,
                                                                    String postcode,
                                                                    String country);
}

