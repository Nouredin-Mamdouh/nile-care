package com.nilecare.dto;

import lombok.Data;

@Data
public class AppointmentResponseDTO {
    private Long id;
    private Long counselorId;
    private String counselorName; // "Dr. Sarah Williams"
    private String date;          // "2025-12-15"
    private String time;          // "10:00 AM"
    private String type = "video";
    private String status;        // "upcoming", "completed"

    // Constructor to make mapping easier
    public AppointmentResponseDTO(Long id, Long counselorId, String counselorName, 
                                  String date, String time, String status) {
        this.id = id;
        this.counselorId = counselorId;
        this.counselorName = counselorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }
}