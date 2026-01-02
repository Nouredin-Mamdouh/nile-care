package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("currentPage", "dashboard");
        addCurrentUser(model, principal);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Principal principal) {
        model.addAttribute("currentPage", "users");
        addCurrentUser(model, principal);
        return "admin/users";
    }

    @GetMapping("/roles")
    public String setRoles(Model model, Principal principal) {
        model.addAttribute("currentPage", "roles");
        addCurrentUser(model, principal);
        return "admin/roles";
    }

    @GetMapping("/reports")
    public String reports(Model model, Principal principal) {
        model.addAttribute("currentPage", "reports");
        addCurrentUser(model, principal);
        return "admin/reports";
    }

    // Helper method to add current admin user to model
    private void addCurrentUser(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                model.addAttribute("currentUser", user);
            }
        }
    }
}