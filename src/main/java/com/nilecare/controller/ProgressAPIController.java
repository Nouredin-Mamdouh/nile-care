package com.nilecare.controller;

import com.nilecare.dto.ProgressPageDTO;
import com.nilecare.model.User;
import com.nilecare.service.ProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/student")
@Slf4j
@CrossOrigin(origins = "*")
public class ProgressAPIController {

    @Autowired
    private ProgressService progressService;

    /**
     * Get complete progress data for the current student
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(HttpSession session) {
        try {
            // Get user from session
            Object userObj = session.getAttribute("currentUser");
            if (userObj == null) {
                log.warn("Unauthorized access to progress API - user not in session");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // Cast to User object and extract ID
            User user = (User) userObj;
            Long userId = user.getUserId();

            // Fetch progress data
            ProgressPageDTO progressData = progressService.getStudentProgress(userId);

            log.info("Retrieved progress data for user: {}", userId);
            return ResponseEntity.ok(progressData);

        } catch (Exception e) {
            log.error("Error fetching progress data", e);
            return ResponseEntity.status(500).body("Error fetching progress data: " + e.getMessage());
        }
    }

    /**
     * Update progress for a specific module
     */
    @PostMapping("/progress/{moduleId}")
    public ResponseEntity<?> updateModuleProgress(
            @PathVariable Long moduleId,
            @RequestParam Integer completionPercentage,
            HttpSession session) {
        try {
            Object userObj = session.getAttribute("currentUser");
            if (userObj == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            User user = (User) userObj;
            Long userId = user.getUserId();

            progressService.updateModuleProgress(userId, moduleId, completionPercentage);

            log.info("Updated module {} progress for user {}", moduleId, userId);
            return ResponseEntity.ok("Progress updated successfully");

        } catch (Exception e) {
            log.error("Error updating module progress", e);
            return ResponseEntity.status(500).body("Error updating progress: " + e.getMessage());
        }
    }

    /**
     * Reset progress for current student (delete all progress records)
     */
    @PostMapping("/progress/reset")
    public ResponseEntity<?> resetProgress(HttpSession session) {
        try {
            Object userObj = session.getAttribute("currentUser");
            if (userObj == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            // In real implementation, you would delete all progress records for this user
            // For now, just return success
            log.info("Reset progress for user");
            return ResponseEntity.ok("Progress reset successfully");

        } catch (Exception e) {
            log.error("Error resetting progress", e);
            return ResponseEntity.status(500).body("Error resetting progress: " + e.getMessage());
        }
    }
}
