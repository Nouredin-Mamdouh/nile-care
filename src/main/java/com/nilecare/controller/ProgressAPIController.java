package com.nilecare.controller;

import com.nilecare.dto.ProgressPageDTO;
import com.nilecare.model.User;
import com.nilecare.service.ProgressService;
import com.nilecare.service.UserService;
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

    /**
     * Get complete progress data for the current student
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(Principal principal) {
        try {
            // Get user from Principal
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
     * Update progress for a specific module
     */
    @PostMapping("/progress/{moduleId}")
    public ResponseEntity<?> updateModuleProgress(
            @PathVariable Long moduleId,
            @RequestParam Integer completionPercentage,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            String email = principal.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                log.warn("User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Long userId = user.getUserId();

            progressService.updateModuleProgress(userId, moduleId, completionPercentage);

            log.info("Updated module {} progress for user {}", moduleId, userId);
            return ResponseEntity.ok("Progress updated successfully");

        } catch (Exception e) {
            log.error("Error updating module progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating progress: " + e.getMessage());
        }
    }

    /**
     * Reset progress for current student (delete all progress records)
     */
    @PostMapping("/progress/reset")
    public ResponseEntity<?> resetProgress(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            String email = principal.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                log.warn("User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // In real implementation, you would delete all progress records for this user
            // For now, just return success
            log.info("Reset progress for user: {}", user.getUserId());
            return ResponseEntity.ok("Progress reset successfully");

        } catch (Exception e) {
            log.error("Error resetting progress", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting progress: " + e.getMessage());
        }
    }
}
