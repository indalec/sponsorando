package com.sponsorando.app.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeController {

    private final String stripePublicKey;

    public StripeController( @Value("${stripe.public.key}")String stripePublicKey) {
        this.stripePublicKey = stripePublicKey;
    }

    @GetMapping("/api/stripe-public-key")
    public ResponseEntity<String> getStripePublicKey() {
        if (stripePublicKey == null || stripePublicKey.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe public key is not configured");
        }
        return ResponseEntity.ok(stripePublicKey.trim());
    }
}
