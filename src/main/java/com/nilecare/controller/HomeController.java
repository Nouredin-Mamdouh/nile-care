package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root(Principal principal) {
        // 1. If User is already logged in, skip Home and go to their Dashboard
        if (principal != null) {
            return "redirect:/learning";
        }
        
        // 2. If Guest, show the Landing Page
        return "home"; 
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        // Redirect to appropriate dashboard based on user role
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return "redirect:/login";
        }

        // Check user role and redirect accordingly
        if (user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_COUNSELOR"))) {
            return "redirect:/counselor/dashboard";
        } else {
            // Default to student dashboard
            return "redirect:/learning";
        }
    }
}