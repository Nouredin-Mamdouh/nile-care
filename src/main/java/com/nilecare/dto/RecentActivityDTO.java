package com.nilecare.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentActivityDTO {
    private String title;
    private String time;      // e.g., "2 hours ago"
    private int completion;   // e.g., 100
    private String color;     // e.g., "#10b981"
    private boolean completed;
}