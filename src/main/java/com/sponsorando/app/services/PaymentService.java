package com.sponsorando.app.services;

import com.sponsorando.app.models.Donation;
import com.sponsorando.app.models.Payment;
import com.sponsorando.app.models.PaymentProvider;
import com.sponsorando.app.models.PaymentStatus;
import com.sponsorando.app.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final Map<PaymentProvider, Object> paymentServices;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, StripePaymentService stripePaymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentServices = Map.of(PaymentProvider.STRIPE, stripePaymentService);
    }

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DonationService donationService;

    @Transactional(readOnly = true)
    public Page<Payment> getAllPayments(int pageNumber, int pageSize) {
        logger.info("Fetching all payments");
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return paymentRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error fetching payments", e);
            throw new RuntimeException("Failed to retrieve payments", e);
        }
    }

    @Transactional
    public Payment processPayment(String paymentProvider, String token, Donation donation) {
        logger.info("Processing payment for donation ID: {} with provider: {}", donation.getId(), paymentProvider);

        try {
            PaymentProvider provider = PaymentProvider.valueOf(paymentProvider.toUpperCase());
            Object service = paymentServices.get(provider);

            if (service == null) {
                throw new IllegalArgumentException("Unsupported payment provider: " + paymentProvider);
            }

            Payment payment;
            if (service instanceof StripePaymentService) {
                payment = ((StripePaymentService) service).processPayment(token, donation);
            } else {
                throw new IllegalArgumentException("Unsupported payment service type");
            }

            payment = paymentRepository.save(payment);
            logger.info("Payment processed and saved successfully for donation ID: {}", donation.getId());

            if (payment.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
                campaignService.updateCampaignCollectedAmount(donation.getCampaign().getId(), payment.getNetConvertedToCampaignCurrency());
            }
            return payment;
        } catch (Exception e) {
            logger.error("Error processing payment for donation ID: {}", donation.getId(), e);
            donationService.deleteDonation(donation.getId());
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long id) {
        logger.info("Fetching payment with ID: {}", id);
        return paymentRepository.findById(id);
    }
}
