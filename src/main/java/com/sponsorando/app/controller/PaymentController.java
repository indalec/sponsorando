package com.sponsorando.app.controller;

import com.sponsorando.app.models.Payment;
import com.sponsorando.app.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String payments(Model model, @RequestParam(name = "page", defaultValue = "0") int pageNumber) {

        String currentUser = (String) model.getAttribute("username");
        String currentRole = (String) model.getAttribute("currentRole");

        int pageSize = 10;

        Page<Payment> page = paymentService.getAllPayments(pageNumber, pageSize);

        model.addAttribute("payments", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber());

        if (currentUser != null && currentRole != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("currentRole", currentRole);
        }

        return "payments";
    }
}
