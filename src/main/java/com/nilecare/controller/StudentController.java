package com.nilecare.controller;

import com.nilecare.repository.LearningModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    @Autowired
    private LearningModuleRepository learningModuleRepository;

    // 1. Learning Library (The page you got 404 on)
    @GetMapping("/learning")
    public String library(Model model) {
        // Fetch modules from DB so th:each="${modules}" works
        model.addAttribute("modules", learningModuleRepository.findAll());
        return "student/library"; // Looks for views/student/library.html
    }

    // 2. Track Progress
    @GetMapping("/progress")
    public String trackProgress(Model model, javax.servlet.http.HttpSession session) {
        // Pass user to model for frontend access if needed
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        return "student/progress";
    }

    // 3. Counseling
    @GetMapping("/counseling")
    public String counseling() {
        return "student/counseling";
    }

    // 4. Assessment
    @GetMapping("/assessment")
    public String assessment() {
        return "student/assessment";
    }

    // 5. Chatbot
    @GetMapping("/chat")
    public String chatbot() {
        return "student/chatbot";
    }

    // 6. Feedback
    @GetMapping("/feedback")
    public String feedback() {
        return "student/feedback";
    }
    
    // 7. Help Request
    @GetMapping("/support/request")
    public String helpRequest() {
        return "student/help_request";
    }
}