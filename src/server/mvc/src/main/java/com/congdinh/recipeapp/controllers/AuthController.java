package com.congdinh.recipeapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.congdinh.recipeapp.dtos.auth.RegisterDTO;
import com.congdinh.recipeapp.sevices.AuthService;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String register(Model model) {
        var registerDTO = new RegisterDTO();
        model.addAttribute("registerDTO", registerDTO);
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute("registerDTO") RegisterDTO registerDTO) {
        authService.save(registerDTO);
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}
