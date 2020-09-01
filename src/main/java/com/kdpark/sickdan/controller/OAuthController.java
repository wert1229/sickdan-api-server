package com.kdpark.sickdan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuthController {

    @GetMapping("/oauth/naverCallback/redirect")
    public String naverCallback(@RequestParam String code, @RequestParam String state, Model model) {
        model.addAttribute("provider", "naver");
        model.addAttribute("code", code);

        return "oauth";
    }
}
