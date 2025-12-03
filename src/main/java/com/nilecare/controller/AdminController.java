package com.nilecare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard"); // HIGHLIGHT SIDEBAR
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("currentPage", "users");
        return "admin/users";
    }

    @GetMapping("/roles")
    public String setRoles(Model model) {
        model.addAttribute("currentPage", "roles");
        return "admin/roles";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("currentPage", "reports");
        return "admin/reports";
    }
}