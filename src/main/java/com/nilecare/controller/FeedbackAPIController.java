package com.nilecare.controller;

import com.nilecare.dto.FeedbackPageDTO;
import com.nilecare.model.StudentFeedback;
import com.nilecare.model.User;
import com.nilecare.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student/feedback")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeedbackAPIController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackAPIController.class);

    @Autowired
    private FeedbackService feedbackService;

    /**
     * GET /api/student/feedback - Fetch all feedback for authenticated student
     */
    @GetMapping
    public ResponseEntity<?> getFeedback(HttpSession session) {
        try {
            User student = (User) session.getAttribute("currentUser");
            
            if (student == null) {
                logger.warn("Unauthorized access to feedback endpoint");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }

            logger.info("Fetching feedback for user: {}", student.getEmail());
            FeedbackPageDTO feedbackData = feedbackService.getFeedbackPageData(student);
            
            return ResponseEntity.ok(feedbackData);

        } catch (Exception e) {
            logger.error("Error fetching feedback: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading feedback: " + e.getMessage()));
        }
    }

    /**
     * POST /api/student/feedback - Submit new feedback
     */
    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestParam String category,
            @RequestParam Integer rating,
            @RequestParam String subject,
            @RequestParam String message,
            HttpSession session) {
        try {
            User student = (User) session.getAttribute("currentUser");
            
            if (student == null) {
                logger.warn("Unauthorized access to submit feedback endpoint");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }

            // Validate input
            if (category == null || category.isEmpty() || 
                rating == null || rating < 1 || rating > 5 ||
                subject == null || subject.isEmpty() ||
                message == null || message.isEmpty()) {
                
                logger.warn("Invalid feedback input from user: {}", student.getEmail());
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid feedback data. All fields are required and rating must be 1-5."));
            }

            logger.info("Submitting feedback from user: {}", student.getEmail());
            StudentFeedback feedback = feedbackService.submitFeedback(
                student, category, rating, subject, message
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thank you for your feedback!");
            response.put("feedbackId", feedback.getFeedbackId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error submitting feedback: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error submitting feedback: " + e.getMessage()));
        }
    }

    /**
     * POST /api/student/feedback/{feedbackId}/respond - Add response to feedback (admin only)
     */
    @PostMapping("/{feedbackId}/respond")
    public ResponseEntity<?> respondToFeedback(
            @PathVariable Long feedbackId,
            @RequestParam String response,
            HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }

            logger.info("Adding response to feedback ID: {}", feedbackId);
            StudentFeedback feedback = feedbackService.addResponse(feedbackId, response);

            if (feedback == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Feedback not found"));
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Response added successfully");
            
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Error responding to feedback: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error responding to feedback: " + e.getMessage()));
        }
    }

    /**
     * GET /api/student/feedback/stats - Get feedback statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getFeedbackStats(HttpSession session) {
        try {
            User student = (User) session.getAttribute("currentUser");
            
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }

            logger.info("Fetching feedback stats for user: {}", student.getEmail());
            Object stats = feedbackService.getFeedbackStatistics(student);
            
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error fetching feedback stats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error loading statistics: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/student/feedback/{feedbackId} - Delete feedback
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(
            @PathVariable Long feedbackId,
            HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }

            logger.info("Deleting feedback ID: {}", feedbackId);
            feedbackService.deleteFeedback(feedbackId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Feedback deleted successfully");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting feedback: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error deleting feedback: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        return error;
    }
}
