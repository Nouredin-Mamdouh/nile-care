package com.nilecare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressPageDTO {
    private Integer overall;
    private Integer modulesCompleted;
    private Integer modulesTotal;
    private Integer streak;
    private Integer totalMinutes;
    private List<WeeklyActivityDTO> weeklyActivity;
    private ModuleDistributionDTO moduleDistribution;
    private List<RecentActivityDTO> recentActivities;
    private List<AchievementDTO> achievements;
}
