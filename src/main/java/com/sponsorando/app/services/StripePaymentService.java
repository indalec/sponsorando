package com.sponsorando.app.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.sponsorando.app.models.Donation;
import com.sponsorando.app.models.Payment;
import com.sponsorando.app.models.PaymentProvider;
import com.sponsorando.app.models.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public Payment processPayment(String token, Donation donation) {
        Stripe.apiKey = stripeSecretKey;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (donation.getAmount() * 100)); // Convert to smallest currency unit
            params.put("currency", donation.getCurrency().getCode().toLowerCase());
            params.put("source", token);

            Charge charge = Charge.create(params);

            Payment payment = new Payment();
            payment.setDonation(donation);
            payment.setTransactionId(charge.getId());
            payment.setPaymentStatus(charge.getStatus().equalsIgnoreCase("succeeded") ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED);
            payment.setGrossAmount(charge.getAmount().doubleValue() / 100); // Convert from smallest currency unit
            payment.setCurrency(donation.getCurrency());
            payment.setPaymentProvider(PaymentProvider.STRIPE);
            payment.setPaymentMethod(charge.getPaymentMethod());
            payment.setFailureMessage(charge.getFailureMessage());
            payment.setTransactionDate(LocalDateTime.now());

            BalanceTransaction balanceTransaction = BalanceTransaction.retrieve(charge.getBalanceTransaction());
            if (balanceTransaction != null) {
                double stripeFee = balanceTransaction.getFee() / 100.0; // Convert from smallest currency unit
                payment.setServiceFee(stripeFee);

                // Net amount is already calculated by Stripe in the charge currency
                double netAmount = balanceTransaction.getNet() / 100.0; // Convert from smallest currency unit
                payment.setNetAmount(netAmount);

                double transactionFee = payment.getGrossAmount() - netAmount - stripeFee;
                payment.setTransactionFee(transactionFee);

                if (balanceTransaction.getExchangeRate() != null) {
                    payment.setExchangeRatePaymentProvider(balanceTransaction.getExchangeRate().doubleValue());
                }

                if (!donation.getCurrency().equals(donation.getCampaign().getCurrency())) {
                    double exchangeRate = payment.getExchangeRatePaymentProvider() != null
                            ? payment.getExchangeRatePaymentProvider()
                            : 1.0;
                    double netConvertedToCampaignCurrency = netAmount * exchangeRate;
                    payment.setNetConvertedToCampaignCurrency(netConvertedToCampaignCurrency);
                    payment.setExchangeRate(exchangeRate);
                } else {
                    payment.setNetConvertedToCampaignCurrency(netAmount);
                    payment.setExchangeRate(1.0);
                }
            } else {
                payment.setServiceFee(0.0);
                payment.setTransactionFee(0.0);
                payment.setNetAmount(payment.getGrossAmount());
                payment.setNetConvertedToCampaignCurrency(payment.getGrossAmount());
                payment.setExchangeRate(1.0);
            }

            return payment;
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment processing failed", e);
        }
    }
}
