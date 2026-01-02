package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * ProfileController handles all user profile-related routing
 * Manages user preferences and settings
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    /**
     * Display user profile page
     * Shows personal details, account settings, and privacy options
     * 
     * @param principal Spring Security Principal containing authenticated user info
     * @param model Spring MVC model
     * @return profile view
     */
    @GetMapping
    public String showProfile(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("currentUser", user);
            }
        }
        return "profile/profile";
    }

    /**
     * Display user preferences page
     * Allows users to customize general, learning, counseling, and accessibility settings
     * 
     * @param principal Spring Security Principal containing authenticated user info
     * @param model Spring MVC model
     * @return preferences view
     */
    @GetMapping("/preferences")
    public String showPreferences(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("currentUser", user);
            }
        }
        return "profile/user-preferences";
    }
}
