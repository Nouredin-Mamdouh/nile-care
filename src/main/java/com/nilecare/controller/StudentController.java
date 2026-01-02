package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.repository.LearningModuleRepository;
import com.nilecare.repository.LessonRepository;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class StudentController {

    @Autowired
    private LearningModuleRepository learningModuleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private UserService userService;

    // 1. Learning Library
    @GetMapping("/learning")
    public String library(Model model, Principal principal) {
        model.addAttribute("modules", learningModuleRepository.findAll());
        addCurrentUser(model, principal);
        return "student/library";
    }

    // 2. Track Progress
    @GetMapping("/progress")
    public String trackProgress(Model model, Principal principal) {
        addCurrentUser(model, principal);
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

    // 8. FAQ
    @GetMapping("/support/faq")
    public String faq() {
        return "student/faq";
    }

    // 9. Analytics
    @GetMapping("/analytics")
    public String analytics(Model model, Principal principal) {
        addCurrentUser(model, principal);
        return "student/analytics";
    }

    // 10. Module Details
    @GetMapping("/modules/{id}")
    public String moduleDetails(@PathVariable Long id, Model model) {
        com.nilecare.model.LearningModule module = learningModuleRepository.findById((Long) id).orElse(null);
        if (module != null) {
            model.addAttribute("module", module);
        }
        return "student/module_details";
    }
    
    // 11. Lesson View
    @GetMapping("/lesson/{id}")
    public String lessonView(@PathVariable Long id, Model model) {
        com.nilecare.model.Lesson lesson = lessonRepository.findByLessonId(id);
        if (lesson != null) {
            model.addAttribute("lesson", lesson);
            model.addAttribute("moduleLessons", lessonRepository.findByModuleIdOrderByLessonOrder(lesson.getModuleId()));
        }
        return "student/lesson_view";
    }
    
    // Helper method to add current user to model
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