package com.nilecare.controller;

import com.nilecare.dto.HelpRequestDTO;
import com.nilecare.model.User;
import com.nilecare.service.HelpRequestService;
import com.nilecare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/help-requests")
@CrossOrigin(origins = "*")
public class HelpRequestAPIController {

    private static final Logger logger = LoggerFactory.getLogger(HelpRequestAPIController.class);

    @Autowired
    private HelpRequestService helpRequestService;

    @Autowired
    private UserService userService;

    /**
     * Submit a new help request
     */
    @PostMapping
    public ResponseEntity<?> submitHelpRequest(
            @RequestBody Map<String, String> payload,
            Principal principal) {
        try {
            logger.info("Received help request submission: {}", payload);
            
            if (principal == null) {
                logger.warn("Unauthorized help request submission - no principal");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            
            if (student == null) {
                logger.warn("User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            String category = payload.get("category");
            String subject = payload.get("subject");
            String message = payload.get("message");

            if (category == null || category.trim().isEmpty() ||
                subject == null || subject.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {
                logger.warn("Invalid help request input - missing fields");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "All fields are required"));
            }

            logger.info("Saving help request for user: {}", student.getEmail());
            HelpRequestDTO savedRequest = helpRequestService.submitHelpRequest(student.getUserId(), category, subject, message);
            logger.info("Help request saved successfully with ID: {}", savedRequest.getRequestId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Help request submitted successfully", "data", savedRequest));
        } catch (Exception e) {
            logger.error("Error submitting help request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get all help requests for the logged-in user
     */
    @GetMapping
    public ResponseEntity<?> getHelpRequests(Principal principal) {
        try {
            if (principal == null) {
                logger.warn("Unauthorized help request fetch - no principal");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            
            if (student == null) {
                logger.warn("User not found: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            logger.info("Fetching help requests for user: {}", student.getEmail());
            List<HelpRequestDTO> requests = helpRequestService.getHelpRequests(student.getUserId());
            logger.info("Found {} help requests for user: {}", requests.size(), student.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching help requests", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    /**
     * Get help requests by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getHelpRequestsByStatus(
            @PathVariable String status,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            List<HelpRequestDTO> requests = helpRequestService.getHelpRequestsByStatus(student.getUserId(), status);
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get a single help request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHelpRequest(@PathVariable Long id, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            HelpRequestDTO request = helpRequestService.getHelpRequest(id);
            return ResponseEntity.ok(Map.of("success", true, "data", request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Update help request status and response (admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHelpRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            String status = payload.get("status");
            String response = payload.get("response");

            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Status is required"));
            }

            HelpRequestDTO updatedRequest = helpRequestService.updateHelpRequest(id, status, response);
            return ResponseEntity.ok(Map.of("success", true, "message", "Help request updated successfully", "data", updatedRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get count of help requests by status
     */
    @GetMapping("/count/{status}")
    public ResponseEntity<?> getRequestCount(
            @PathVariable String status,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            long count = helpRequestService.getRequestCountByStatus(student.getUserId(), status);
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get resolution rate for user
     */
    @GetMapping("/resolution-rate")
    public ResponseEntity<?> getResolutionRate(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            String email = principal.getName();
            User student = userService.findByEmail(email);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not found"));
            }

            double rate = helpRequestService.getResolutionRate(student.getUserId());
            return ResponseEntity.ok(Map.of("success", true, "resolutionRate", rate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
