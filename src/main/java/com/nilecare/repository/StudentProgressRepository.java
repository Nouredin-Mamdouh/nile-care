package com.nilecare.repository;

import com.nilecare.model.StudentProgress;
import com.nilecare.model.User;
import com.nilecare.model.LearningModule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    // ==========================================
    // 1. EXISTING METHODS (Kept as they are useful)
    // ==========================================
    List<StudentProgress> findByStudent(User student);

    Optional<StudentProgress> findByStudentAndModule(User student, LearningModule module);

    @Query("SELECT COUNT(sp) FROM StudentProgress sp WHERE sp.student = :student AND sp.status = 'COMPLETED'")
    Integer countCompletedModulesByStudent(@Param("student") User student);

    @Query("SELECT COUNT(sp) FROM StudentProgress sp WHERE sp.student = :student AND sp.status IN ('COMPLETED', 'IN_PROGRESS')")
    Integer countModulesInProgressOrCompleted(@Param("student") User student);

    @Query("SELECT COUNT(sp) FROM StudentProgress sp WHERE sp.student = :student AND sp.status = 'NOT_STARTED'")
    Integer countNotStartedModules(@Param("student") User student);

    @Query("SELECT AVG(sp.completionPercentage) FROM StudentProgress sp WHERE sp.student = :student")
    Double getAverageCompletionPercentage(@Param("student") User student);

    @Query("SELECT SUM(CAST(sp.completionPercentage AS int) / 100 * 60) FROM StudentProgress sp WHERE sp.student = :student AND sp.lastAccessed > :since")
    Integer getTotalMinutesSince(@Param("student") User student, @Param("since") LocalDateTime since);

    @Query("SELECT sp FROM StudentProgress sp WHERE sp.student = :student ORDER BY sp.lastAccessed DESC")
    List<StudentProgress> findRecentActivities(@Param("student") User student, Pageable pageable);

    long countByStatus(String status);


    // ==========================================
    // 2. NEW METHODS (Required for ProgressService)
    // ==========================================

    /**
     * Find all progress for a student using just their ID.
     * (Matches 'studentProgressRepo.findByUserId' in Service)
     */
    List<StudentProgress> findByStudent_UserId(Long userId);

    /**
     * Find specific module progress using just IDs.
     * (Matches 'studentProgressRepo.findByUserIdAndModuleId' in Service)
     */
    @Query("SELECT sp FROM StudentProgress sp WHERE sp.student.userId = :userId AND sp.module.moduleId = :moduleId")
    StudentProgress findByUserIdAndModuleId(@Param("userId") Long userId, @Param("moduleId") Long moduleId);
    
    // Note: If you prefer Optional, change return type to Optional<StudentProgress>
    // But our current Service expects a direct object or null.
}