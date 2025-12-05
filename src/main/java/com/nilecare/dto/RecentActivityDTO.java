package com.nilecare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String title;
    private Integer completion;
    private String time;
    private Boolean completed;
    private String color;
}
