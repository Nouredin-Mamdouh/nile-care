package com.nilecare.controller;

import com.nilecare.dto.AppointmentRequestDTO;
import com.nilecare.model.User;
import com.nilecare.service.CounselingService;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/counseling")
public class CounselingAPIController {

    @Autowired
    private CounselingService counselingService;
    @Autowired
    private UserService userService;

    // 1. Get My Appointments
    @GetMapping("/my-appointments")
    public ResponseEntity<?> getMyAppointments(Principal principal) {
        User student = userService.findByEmail(principal.getName());
        if (student == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }
        return ResponseEntity.ok(counselingService.getStudentAppointments(student.getUserId()));
    }

    // 2. Book Appointment
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequestDTO request, Principal principal) {
        try {
            User student = userService.findByEmail(principal.getName());
            if (student == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
            }

            counselingService.bookAppointment(student, request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment booked successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}