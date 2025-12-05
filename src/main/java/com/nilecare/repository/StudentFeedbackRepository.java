package com.nilecare.repository;

import com.nilecare.model.StudentFeedback;
import com.nilecare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentFeedbackRepository extends JpaRepository<StudentFeedback, Long> {
    
    /**
     * Get all feedback for a student ordered by creation date (newest first)
     */
    @Query("SELECT sf FROM StudentFeedback sf WHERE sf.student = :student ORDER BY sf.createdAt DESC")
    Page<StudentFeedback> findByStudent(@Param("student") User student, Pageable pageable);

    /**
     * Count feedback submitted by a student
     */
    @Query("SELECT COUNT(sf) FROM StudentFeedback sf WHERE sf.student = :student")
    Integer countByStudent(@Param("student") User student);

    /**
     * Get average rating for a specific category
     */
    @Query("SELECT AVG(sf.rating) FROM StudentFeedback sf WHERE sf.category = :category")
    Double getAverageRatingByCategory(@Param("category") String category);

    /**
     * Count feedback by category
     */
    @Query("SELECT COUNT(sf) FROM StudentFeedback sf WHERE sf.category = :category")
    Integer countByCategory(@Param("category") String category);

    /**
     * Get overall average rating
     */
    @Query("SELECT AVG(sf.rating) FROM StudentFeedback sf")
    Double getOverallAverageRating();

    /**
     * Count feedback by status
     */
    @Query("SELECT COUNT(sf) FROM StudentFeedback sf WHERE sf.status = :status")
    Integer countByStatus(@Param("status") StudentFeedback.FeedbackStatus status);

    /**
     * Get feedback with responses (responded status)
     */
    @Query("SELECT sf FROM StudentFeedback sf WHERE sf.student = :student AND sf.status = 'RESPONDED' ORDER BY sf.updatedAt DESC")
    List<StudentFeedback> findRespondedFeedback(@Param("student") User student);

    /**
     * Get pending feedback for a student
     */
    @Query("SELECT sf FROM StudentFeedback sf WHERE sf.student = :student AND sf.status = 'PENDING' ORDER BY sf.createdAt DESC")
    List<StudentFeedback> findPendingFeedback(@Param("student") User student);

    /**
     * Get feedback created within date range
     */
    @Query("SELECT sf FROM StudentFeedback sf WHERE sf.student = :student AND sf.createdAt BETWEEN :startDate AND :endDate ORDER BY sf.createdAt DESC")
    List<StudentFeedback> findFeedbackInDateRange(@Param("student") User student, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
}
