package com.nilecare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean earned;
    private String date;
}
