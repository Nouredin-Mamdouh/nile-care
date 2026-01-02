package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.model.Role;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // --- LOGIN ---
    @GetMapping("/login")
    public String loginPage() {
        return "auth/auth"; // Show login form
    }

    // Spring Security handles POST /login automatically - no need for manual handling
    // The form posts to /login with username and password parameters
    // Spring Security validates credentials using CustomUserDetailsService
    // On success: redirect to defaultSuccessUrl (/dashboard)
    // On failure: redirect to /login?error=true

    // --- REGISTER ---
    @GetMapping("/register")
    public String registerPage() {
        // Redirect to login page with register view flag
        return "redirect:/login?view=register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String fullName, 
                                 @RequestParam String email, 
                                 @RequestParam String password, 
                                 @RequestParam String role,
                                 Model model) {
        
        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email already registered!");
            return "auth/auth"; 
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        
        Role.RoleType roleType = role.equals("ADMIN") ? Role.RoleType.ROLE_ADMIN : Role.RoleType.ROLE_STUDENT;
        
        userService.registerUser(newUser, password, roleType);
        
        return "redirect:/login?registered=true";
    }

    // --- LOGOUT ---
    // Spring Security handles GET /logout and POST /logout automatically
    // No need for manual logout handler
    // Configured in SecurityConfig:
    // - Invalidates session
    // - Clears JSESSIONID cookie
    // - Redirects to /login?logout=true
}