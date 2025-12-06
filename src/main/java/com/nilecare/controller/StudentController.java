package com.nilecare.controller;

import com.nilecare.repository.LearningModuleRepository;
import com.nilecare.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StudentController {

    @Autowired
    private LearningModuleRepository learningModuleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;

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

    // 8. FAQ
    @GetMapping("/support/faq")
    public String faq() {
        return "student/faq";
    }

    // 9. Analytics
    @GetMapping("/analytics")
    public String analytics(Model model, javax.servlet.http.HttpSession session) {
        // Pass user to model for frontend access if needed
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        return "student/analytics";
    }

    // 10. Module Details
    @GetMapping("/modules/{id}")
    public String moduleDetails(@PathVariable Long id, Model model) {
        // Fetch the specific module from DB
        com.nilecare.model.LearningModule module = learningModuleRepository.findById(id).orElse(null);
        if (module != null) {
            model.addAttribute("module", module);
        }
        return "student/module_details";
    }
    
    // 11. Lesson View
    @GetMapping("/lesson/{id}")
    public String lessonView(@PathVariable Long id, Model model) {
        // Fetch the specific lesson from DB
        com.nilecare.model.Lesson lesson = lessonRepository.findByLessonId(id);
        if (lesson != null) {
            model.addAttribute("lesson", lesson);
            // Also fetch all lessons from the same module for sidebar navigation
            model.addAttribute("moduleLessons", lessonRepository.findByModuleIdOrderByLessonOrder(lesson.getModuleId()));
        }
        return "student/lesson_view";
    }
}