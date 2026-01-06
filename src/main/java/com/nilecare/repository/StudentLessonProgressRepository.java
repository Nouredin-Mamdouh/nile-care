package com.nilecare.repository;

import com.nilecare.model.StudentLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, Long> {
    
    // Check if a specific lesson is already done by this user
    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

    // [FIXED] Changed 'ProgressId' to 'Id' to match your Entity
    List<StudentLessonProgress> findTop5ByUserIdOrderByIdDesc(Long userId);
    
    // Count total completed lessons to estimate "Total Minutes"
    long countByUserId(Long userId);
    
    // Count how many lessons this user has finished in a specific module
    long countByUserIdAndModuleId(Long userId, Long moduleId);
}