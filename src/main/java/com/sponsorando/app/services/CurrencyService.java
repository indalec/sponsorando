package com.sponsorando.app.services;

import com.sponsorando.app.models.Currency;
import com.sponsorando.app.repositories.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Transactional(readOnly = true)
    public List<Currency> getCurrencies() {
        logger.info("Fetching all currencies");
        try {
            return currencyRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching currencies", e);
            throw new RuntimeException("Failed to retrieve currencies", e);
        }
    }
}
