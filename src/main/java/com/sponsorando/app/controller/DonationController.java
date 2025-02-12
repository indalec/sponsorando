package com.sponsorando.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonationController {

    @GetMapping("/donations")
    public String donations() {
        return "donations";
    }
}
