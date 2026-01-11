package com.nilecare.dto;

import lombok.Data;

@Data
public class AppointmentRequestDTO {
    private Long counselorId;
    private String date;   // "2025-12-15"
    private String time;   // "10:00 AM"
    private String notes;
}