package com.nilecare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatsDTO {
    private Integer overallProgress;
    private Integer modulesCompleted;
    private Integer modulesTotal;
    private Integer currentStreak;
    private Integer totalMinutes;
    private ModuleDistributionDTO moduleDistribution;
}
