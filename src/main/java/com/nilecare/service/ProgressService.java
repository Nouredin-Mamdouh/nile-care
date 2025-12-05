package com.nilecare.service;

import com.nilecare.dto.*;
import com.nilecare.model.User;
import java.util.List;

public interface ProgressService {
    ProgressPageDTO getStudentProgress(Long userId);
    ProgressStatsDTO getProgressStats(Long userId);
    List<WeeklyActivityDTO> getWeeklyActivity(Long userId);
    List<RecentActivityDTO> getRecentActivities(Long userId);
    List<AchievementDTO> getAchievements(Long userId);
    ModuleDistributionDTO getModuleDistribution(Long userId);
    void updateModuleProgress(Long userId, Long moduleId, Integer completionPercentage);
}
