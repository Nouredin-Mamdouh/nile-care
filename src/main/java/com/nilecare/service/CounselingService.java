package com.nilecare.service;

import com.nilecare.dto.AppointmentRequestDTO;
import com.nilecare.dto.AppointmentResponseDTO;
import com.nilecare.model.*;
import com.nilecare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CounselingService {

    @Autowired
    private AppointmentRepository appointmentRepository; // The one you provided
    @Autowired
    private CounselorAvailabilityRepository availabilityRepository;
    @Autowired
    private UserRepository userRepository;

    // Date Formatters (Shared constant to ensure consistency)
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

    /**
     * 1. BOOKING LOGIC
     */
    @Transactional
    public void bookAppointment(User student, AppointmentRequestDTO request) {
        // Find Counselor
        User counselor = userRepository.findById(request.getCounselorId())
                .orElseThrow(() -> new RuntimeException("Counselor not found"));

        // Parse Strings to LocalDateTime
        LocalDate datePart = LocalDate.parse(request.getDate());
        LocalTime timePart = LocalTime.parse(request.getTime(), TIME_FORMATTER);
        LocalDateTime startDateTime = LocalDateTime.of(datePart, timePart);
        LocalDateTime endDateTime = startDateTime.plusHours(1);

        // Find or Create Slot (Lazy Creation)
        CounselorAvailability slot = availabilityRepository.findByCounselorAndStartTime(counselor, startDateTime)
                .orElseGet(() -> {
                    CounselorAvailability newSlot = new CounselorAvailability();
                    newSlot.setCounselor(counselor);
                    newSlot.setStartTime(startDateTime);
                    newSlot.setEndTime(endDateTime);
                    newSlot.setBooked(false);
                    return availabilityRepository.save(newSlot);
                });

        if (slot.isBooked()) {
            throw new RuntimeException("Slot already booked");
        }

        // Create Appointment
        Appointment appointment = new Appointment();
        appointment.setStudent(student);
        appointment.setSlot(slot);
        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.Status.CONFIRMED);

        // Save
        slot.setBooked(true);
        availabilityRepository.save(slot);
        appointmentRepository.save(appointment);
    }

    /**
     * 2. HISTORY LOGIC (Uses your Repository method)
     */
    public List<AppointmentResponseDTO> getStudentAppointments(Long studentId) {
        // Use the method you provided in the prompt
        List<Appointment> appointments = appointmentRepository.findByStudent_UserId(studentId);

        // Convert Entities to DTOs for the Frontend
        return appointments.stream().map(appt -> {
            CounselorAvailability slot = appt.getSlot();

            // Extract Date and Time separately from LocalDateTime
            String dateStr = slot.getStartTime().toLocalDate().toString(); // "2025-12-15"
            String timeStr = slot.getStartTime().toLocalTime().format(TIME_FORMATTER); // "10:00 AM"

            // Map Status (CONFIRMED -> upcoming)
            String statusStr = (appt.getStatus() == Appointment.Status.CONFIRMED) ? "upcoming"
                    : (appt.getStatus() == Appointment.Status.CANCELLED) ? "cancelled" : "completed";

            return new AppointmentResponseDTO(
                    appt.getAppointmentId(),
                    slot.getCounselor().getUserId(),
                    slot.getCounselor().getFullName(), // User has getFullName() not getName()
                    dateStr,
                    timeStr,
                    statusStr);
        }).collect(Collectors.toList());
    }
}