package com.sponsorando.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {

    @GetMapping("/payments")
    public String payments() {
        return "payments";
    }
}
