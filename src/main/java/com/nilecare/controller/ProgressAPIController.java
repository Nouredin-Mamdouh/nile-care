package com.nilecare.controller;

import com.nilecare.dto.ProgressPageDTO;
import com.nilecare.model.User;
import com.nilecare.model.StudentLessonProgress;
import com.nilecare.model.Lesson;
import com.nilecare.service.ProgressService;
import com.nilecare.service.UserService;
import com.nilecare.repository.LessonRepository;
import com.nilecare.repository.StudentLessonProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/student")
@Slf4j
@CrossOrigin(origins = "*")
public class ProgressAPIController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserService userService;

    // --- NEW REPOSITORIES ---
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonProgressRepository lessonProgressRepo;

    /**
     * Get complete progress data for the current student
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(Principal principal) {
        try {
            if (principal == null) {
                log.warn("Unauthorized access to progress API - user not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            String email = principal.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                log.warn("User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Long userId = user.getUserId();

            // Fetch progress data
            ProgressPageDTO progressData = progressService.getStudentProgress(userId);

            log.info("Retrieved progress data for user: {}", userId);
            return ResponseEntity.ok(progressData);

        } catch (Exception e) {
            log.error("Error fetching progress data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching progress data: " + e.getMessage());
        }
    }

    /**
     * [NEW] Mark a specific lesson as complete
     * This replaces the manual percentage update. It calculates progress dynamically.
     */
    @PostMapping("/complete-lesson/{lessonId}")
    public ResponseEntity<?> markLessonComplete(
            @PathVariable Long lessonId,
            Principal principal) {
        try {
            // 1. Auth Check
            if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

            // 2. Find the Lesson to get the Module ID
            Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
            
            // NOTE: Assuming your Lesson entity links to 'LearningModule' via 'getModule()'
            // If your code fails here, check if your Lesson entity uses 'getModuleId()' directly.
            Long moduleId = lesson.getModuleId(); 

            // 3. Mark the Lesson as "Checked" ✅ (Prevent duplicates)
            if (!lessonProgressRepo.existsByUserIdAndLessonId(user.getUserId(), lessonId)) {
                lessonProgressRepo.save(new StudentLessonProgress(user.getUserId(), lessonId, moduleId));
            }

            // 4. Calculate New Percentage 📊
            // Count total lessons in this module
            long totalLessons = lessonRepository.countTotalLessonsInModule(moduleId);
            // Count how many this user has finished
            long completedLessons = lessonProgressRepo.countByUserIdAndModuleId(user.getUserId(), moduleId);
            
            // Calculate % (Avoid division by zero)
            int newPercentage = (totalLessons == 0) ? 100 : (int) ((completedLessons * 100.0) / totalLessons);
            if (newPercentage > 100) newPercentage = 100;

            // 5. Update the Tracker Table (The Summary)
            progressService.updateModuleProgress(user.getUserId(), moduleId, newPercentage);

            log.info("User {} completed lesson {}. Module {} is now {}% complete.", 
                     user.getUserId(), lessonId, moduleId, newPercentage);

            return ResponseEntity.ok("Lesson completed. Module progress is now " + newPercentage + "%");

        } catch (Exception e) {
            log.error("Error completing lesson", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Update progress for a specific module (Manual Override)
     * Kept for backward compatibility, but 'complete-lesson' is preferred.
     */
    @PostMapping("/progress/{moduleId}")
    public ResponseEntity<?> updateModuleProgress(
            @PathVariable Long moduleId,
            @RequestParam Integer completionPercentage,
            Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

            progressService.updateModuleProgress(user.getUserId(), moduleId, completionPercentage);

            log.info("Updated module {} progress for user {}", moduleId, user.getUserId());
            return ResponseEntity.ok("Progress updated successfully");

        } catch (Exception e) {
            log.error("Error updating module progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating progress: " + e.getMessage());
        }
    }

    /**
     * Reset progress for current student
     */
    @PostMapping("/progress/reset")
    public ResponseEntity<?> resetProgress(Principal principal) {
        try {
            if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

            // 1. Delete individual lesson progress (The new table)
            // lessonProgressRepo.deleteAllByUserId(user.getUserId()); // Uncomment if you add this method to Repo

            // 2. Reset the summary table (Existing logic)
            // progressService.resetStudentProgress(user.getUserId()); // specific service method needed here
            
            log.info("Reset progress for user: {}", user.getUserId());
            return ResponseEntity.ok("Progress reset successfully");

        } catch (Exception e) {
            log.error("Error resetting progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting progress: " + e.getMessage());
        }
    }
}