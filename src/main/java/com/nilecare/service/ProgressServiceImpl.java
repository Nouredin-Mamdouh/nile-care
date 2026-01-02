package com.nilecare.service;

import com.nilecare.dto.*;
import com.nilecare.model.*;
import com.nilecare.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private StudentProgressRepository progressRepository;

    @Autowired
    private StudentAchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningModuleRepository moduleRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ProgressPageDTO getStudentProgress(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProgressPageDTO dto = new ProgressPageDTO();
        dto.setOverall(calculateOverallProgress(user));
        dto.setModulesCompleted(progressRepository.countCompletedModulesByStudent(user));
        dto.setModulesTotal((int) moduleRepository.count());
        dto.setStreak(calculateCurrentStreak(user));
        dto.setTotalMinutes(calculateTotalMinutes(user));
        dto.setWeeklyActivity(getWeeklyActivity(userId));
        dto.setModuleDistribution(getModuleDistribution(userId));
        dto.setRecentActivities(getRecentActivities(userId));
        dto.setAchievements(getAchievements(userId));

        return dto;
    }

    @Override
    public ProgressStatsDTO getProgressStats(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProgressStatsDTO stats = new ProgressStatsDTO();
        stats.setOverallProgress(calculateOverallProgress(user));
        stats.setModulesCompleted(progressRepository.countCompletedModulesByStudent(user));
        stats.setModulesTotal((int) moduleRepository.count());
        stats.setCurrentStreak(calculateCurrentStreak(user));
        stats.setTotalMinutes(calculateTotalMinutes(user));
        stats.setModuleDistribution(getModuleDistribution(userId));

        return stats;
    }

    @Override
    public List<WeeklyActivityDTO> getWeeklyActivity(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StudentProgress> progressList = progressRepository.findByStudent(user);

        // Create map with days of week
        Map<String, Integer> weeklyMap = new LinkedHashMap<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            weeklyMap.put(day, 0);
        }

        // Calculate minutes for each day (simplified - based on completion percentage)
        LocalDateTime now = LocalDateTime.now();
        for (StudentProgress progress : progressList) {
            if (progress.getLastAccessed() != null) {
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(
                        progress.getLastAccessed().toLocalDate(),
                        now.toLocalDate()
                );
                if (daysDiff < 7) {
                    int dayOfWeek = progress.getLastAccessed().getDayOfWeek().getValue() - 1;
                    String day = days[dayOfWeek];
                    int minutes = (progress.getCompletionPercentage() / 10) + 20; // Simplified calculation
                    weeklyMap.put(day, weeklyMap.get(day) + minutes);
                }
            }
        }

        return weeklyMap.entrySet().stream()
                .map(entry -> new WeeklyActivityDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StudentProgress> progressList = progressRepository.findByStudent(user);

        return progressList.stream()
                .filter(p -> p.getLastAccessed() != null)
                .sorted(Comparator.comparing(StudentProgress::getLastAccessed).reversed())
                .limit(4)
                .map(progress -> {
                    String timeAgo = formatTimeAgo(progress.getLastAccessed());
                    String color = getProgressColor(progress.getCompletionPercentage());
                    return new RecentActivityDTO(
                            progress.getModule().getTitle(),
                            progress.getCompletionPercentage(),
                            timeAgo,
                            progress.getStatus() == StudentProgress.Status.COMPLETED,
                            color
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementDTO> getAchievements(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StudentAchievement> achievements = achievementRepository.findByStudent(user);

        return achievements.stream()
                .map(achievement -> new AchievementDTO(
                        achievement.getAchievementId(),
                        achievement.getTitle(),
                        achievement.getDescription(),
                        achievement.getIsEarned(),
                        achievement.getEarnedAt() != null ? achievement.getEarnedAt().format(DATE_FORMATTER) : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ModuleDistributionDTO getModuleDistribution(Long userId) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer completed = progressRepository.countCompletedModulesByStudent(user);
        Integer inProgress = progressRepository.countModulesInProgressOrCompleted(user) - completed;
        Integer notStarted = progressRepository.countNotStartedModules(user);

        return new ModuleDistributionDTO(completed, inProgress, notStarted);
    }

    @Override
    public void updateModuleProgress(Long userId, Long moduleId, Integer completionPercentage) {
        User user = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LearningModule module = moduleRepository.findById((Long) moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        StudentProgress progress = progressRepository.findByStudentAndModule(user, module)
                .orElse(new StudentProgress());

        progress.setStudent(user);
        progress.setModule(module);
        progress.setCompletionPercentage(completionPercentage);
        progress.setLastAccessed(LocalDateTime.now());

        if (completionPercentage >= 100) {
            progress.setStatus(StudentProgress.Status.COMPLETED);
        } else if (completionPercentage > 0) {
            progress.setStatus(StudentProgress.Status.IN_PROGRESS);
        }

        progressRepository.save(progress);
        log.info("Updated progress for user {} on module {}", userId, moduleId);
    }

    // ============================================
    // Helper Methods
    // ============================================

    private Integer calculateOverallProgress(User user) {
        Double avgCompletion = progressRepository.getAverageCompletionPercentage(user);
        return avgCompletion != null ? avgCompletion.intValue() : 0;
    }

    private Integer calculateCurrentStreak(User user) {
        List<StudentProgress> progressList = progressRepository.findByStudent(user);

        int streak = 0;
        LocalDateTime now = LocalDateTime.now();

        for (StudentProgress progress : progressList) {
            if (progress.getLastAccessed() != null) {
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(
                        progress.getLastAccessed().toLocalDate(),
                        now.toLocalDate()
                );

                if (daysDiff <= 1) {
                    streak++;
                }
            }
        }

        return Math.min(streak, 7); // Cap at 7 days
    }

    private Integer calculateTotalMinutes(User user) {
        Integer totalMinutes = progressRepository.getTotalMinutesSince(user, LocalDateTime.now().minusMonths(1));
        return totalMinutes != null ? totalMinutes : 0;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(dateTime, now);
        long hours = java.time.temporal.ChronoUnit.HOURS.between(dateTime, now);
        long days = java.time.temporal.ChronoUnit.DAYS.between(dateTime, now);

        // Handle edge cases where timestamp might be in the future or just now
        if (minutes <= 0) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days == 1) {
            return "Yesterday";
        } else if (days < 7) {
            return days + " days ago";
        } else {
            return dateTime.format(DATE_FORMATTER);
        }
    }

    private String getProgressColor(Integer completion) {
        if (completion >= 75) {
            return "#22c55e"; // Green
        } else if (completion >= 50) {
            return "#3b82f6"; // Blue
        } else if (completion >= 25) {
            return "#fbbf24"; // Yellow
        } else {
            return "#ef4444"; // Red
        }
    }
}
