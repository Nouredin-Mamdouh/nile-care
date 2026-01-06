package com.nilecare.service;

import com.nilecare.dto.*;
import com.nilecare.model.LearningModule;
import com.nilecare.model.StudentLessonProgress;
import com.nilecare.model.StudentProgress;
import com.nilecare.model.Lesson;
import com.nilecare.model.User;
import com.nilecare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    @Autowired
    private StudentProgressRepository studentProgressRepo; 
    
    @Autowired
    private StudentLessonProgressRepository lessonProgressRepo; 
    
    @Autowired
    private LearningModuleRepository moduleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private UserRepository userRepository;

    public ProgressPageDTO getStudentProgress(Long userId) {
        
        // 1. Fetch Basic Stats
        List<LearningModule> allModules = moduleRepository.findAll();
        List<StudentProgress> userProgress = studentProgressRepo.findByStudent_UserId(userId);
        
        int totalModules = allModules.size();
        int completedModules = 0;
        int inProgressModules = 0;
        int sumPercentages = 0;

        // 2. Calculate Module Distribution
        for (StudentProgress sp : userProgress) {
            int p = (sp.getCompletionPercentage() == null) ? 0 : sp.getCompletionPercentage();
            
            if (p >= 100) {
                completedModules++;
            } else if (p > 0) {
                inProgressModules++;
            }
            sumPercentages += p;
        }

        int notStartedModules = totalModules - (completedModules + inProgressModules);
        if (notStartedModules < 0) notStartedModules = 0; 

        // 3. Calculate Overall Progress
        int overall = (totalModules > 0) ? (sumPercentages / totalModules) : 0;

        // 4. Estimate Total Time (1 Lesson = 30 mins)
        long totalLessonsCompleted = lessonProgressRepo.countByUserId(userId);
        int totalMinutes = (int) (totalLessonsCompleted * 30);

        // 5. Build Recent Activity List
        List<RecentActivityDTO> recentActivities = new ArrayList<>();
        
        // [FIXED] Updated method call to match Repository (OrderByIdDesc)
        List<StudentLessonProgress> recentLogs = lessonProgressRepo.findTop5ByUserIdOrderByIdDesc(userId);

        for (StudentLessonProgress log : recentLogs) {
            String lessonTitle = "Lesson #" + log.getLessonId();
            Optional<Lesson> lessonOpt = lessonRepository.findByLessonId(log.getLessonId()) != null 
                    ? Optional.of(lessonRepository.findByLessonId(log.getLessonId())) 
                    : Optional.empty();
            
            if (lessonOpt.isPresent()) {
                lessonTitle = lessonOpt.get().getTitle();
            }

            recentActivities.add(new RecentActivityDTO(
                lessonTitle,
                "Recently", 
                100,        
                "#10b981",  
                true
            ));
        }

        // 6. Build Final DTO
        ProgressPageDTO dto = new ProgressPageDTO();
        dto.setOverall(overall);
        dto.setModulesCompleted(completedModules);
        dto.setModulesTotal(totalModules);
        dto.setStreak(1); 
        dto.setTotalMinutes(totalMinutes);
        dto.setModuleDistribution(new ModuleDistributionDTO(completedModules, inProgressModules, notStartedModules));
        dto.setRecentActivities(recentActivities);
        dto.setWeeklyActivity(new ArrayList<>()); 
        dto.setAchievements(new ArrayList<>());

        return dto;
    }

    public void updateModuleProgress(Long userId, Long moduleId, int newPercentage) {
        StudentProgress progress = studentProgressRepo.findByUserIdAndModuleId(userId, moduleId);
        
        if (progress == null) {
            progress = new StudentProgress();
            
            User user = userRepository.findById(userId).orElse(null);
            LearningModule module = moduleRepository.findById(moduleId).orElse(null);
            
            if (user != null && module != null) {
                progress.setStudent(user);
                progress.setModule(module);
                progress.setCompletionPercentage(newPercentage);
                progress.setStatus(newPercentage >= 100 ? "COMPLETED" : "IN_PROGRESS");
                progress.setLastAccessed(LocalDateTime.now());
                studentProgressRepo.save(progress);
            }
        } else {
            progress.setCompletionPercentage(newPercentage);
            progress.setStatus(newPercentage >= 100 ? "COMPLETED" : "IN_PROGRESS");
            progress.setLastAccessed(LocalDateTime.now());
            studentProgressRepo.save(progress);
        }
    }
}