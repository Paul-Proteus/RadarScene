package com.radarscene.api.controller;

import com.radarscene.api.dto.RegisterRequest;
import com.radarscene.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for web UI pages
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final AuthService authService;

    /**
     * Home page
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * Process registration
     */
    @PostMapping("/auth/register-process")
    public String processRegistration(@ModelAttribute RegisterRequest registerRequest, 
                                     RedirectAttributes redirectAttributes) {
        try {
            authService.register(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
