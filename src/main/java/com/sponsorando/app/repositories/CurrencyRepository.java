package com.sponsorando.app.repositories;


import com.sponsorando.app.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository  extends JpaRepository<Currency, String > {
}
