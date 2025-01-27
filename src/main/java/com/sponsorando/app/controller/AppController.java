package com.sponsorando.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {


   @GetMapping("/")
    public String index() {
       return "index";
   }

   @GetMapping("/login")
    public String login() {
        return "login";
   }


}
