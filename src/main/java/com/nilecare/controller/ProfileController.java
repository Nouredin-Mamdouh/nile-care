package com.nilecare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ProfileController handles all user profile-related routing
 * Manages user preferences and settings
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    /**
     * Display user profile page
     * Shows personal details, account settings, and privacy options
     * 
     * @return profile view
     */
    @GetMapping
    public String showProfile() {
        return "profile/profile";
    }

    /**
     * Display user preferences page
     * Allows users to customize general, learning, counseling, and accessibility settings
     * 
     * @return preferences view
     */
    @GetMapping("/preferences")
    public String showPreferences() {
        return "profile/user-preferences";
    }
}
