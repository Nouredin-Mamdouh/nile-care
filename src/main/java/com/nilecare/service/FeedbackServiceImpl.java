package com.nilecare.service;

import com.nilecare.dto.CategoryStatsDTO;
import com.nilecare.dto.FeedbackItemDTO;
import com.nilecare.dto.FeedbackPageDTO;
import com.nilecare.dto.FeedbackStatsDTO;
import com.nilecare.model.StudentFeedback;
import com.nilecare.model.User;
import com.nilecare.repository.StudentFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private StudentFeedbackRepository feedbackRepository;

    private static final String[] CATEGORIES = {
        "Learning Modules",
        "Counseling Service",
        "Platform Experience",
        "Self-Assessments",
        "Chatbot",
        "Other"
    };

    @Override
    public StudentFeedback submitFeedback(User student, String category, Integer rating, String subject, String message) {
        StudentFeedback feedback = new StudentFeedback();
        feedback.setStudent(student);
        feedback.setCategory(category);
        feedback.setRating(rating);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        feedback.setStatus(StudentFeedback.FeedbackStatus.PENDING);
        
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackPageDTO getFeedbackPageData(User student) {
        // Get recent feedback (max 50)
        Page<StudentFeedback> feedbackPage = feedbackRepository.findByStudent(student, PageRequest.of(0, 50));
        
        // Convert to DTOs
        List<FeedbackItemDTO> feedbackItems = new ArrayList<>();
        for (StudentFeedback feedback : feedbackPage.getContent()) {
            feedbackItems.add(convertToDTO(feedback));
        }
        
        // Get statistics
        FeedbackStatsDTO stats = calculateFeedbackStats(student);
        
        return new FeedbackPageDTO(feedbackItems, stats);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentFeedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById((Long) feedbackId).orElse(null);
    }

    @Override
    public StudentFeedback updateFeedbackStatus(Long feedbackId, StudentFeedback.FeedbackStatus status) {
        StudentFeedback feedback = feedbackRepository.findById((Long) feedbackId).orElse(null);
        if (feedback != null) {
            feedback.setStatus(status);
            return feedbackRepository.save(feedback);
        }
        return null;
    }

    @Override
    public StudentFeedback addResponse(Long feedbackId, String response) {
        StudentFeedback feedback = feedbackRepository.findById((Long) feedbackId).orElse(null);
        if (feedback != null) {
            feedback.setResponse(response);
            feedback.setStatus(StudentFeedback.FeedbackStatus.RESPONDED);
            return feedbackRepository.save(feedback);
        }
        return null;
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById((Long) feedbackId);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getFeedbackStatistics(User student) {
        return calculateFeedbackStats(student);
    }

    /**
     * Convert StudentFeedback to FeedbackItemDTO
     */
    private FeedbackItemDTO convertToDTO(StudentFeedback feedback) {
        String date = feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return new FeedbackItemDTO(
            feedback.getFeedbackId(),
            feedback.getCategory(),
            feedback.getRating(),
            feedback.getSubject(),
            feedback.getMessage(),
            feedback.getStatus().toString().toLowerCase(),
            date,
            feedback.getResponse()
        );
    }

    /**
     * Calculate feedback statistics
     */
    private FeedbackStatsDTO calculateFeedbackStats(User student) {
        Integer totalFeedback = feedbackRepository.countByStudent(student);
        Double averageRating = feedbackRepository.getOverallAverageRating();
        
        if (averageRating == null) {
            averageRating = 0.0;
        }
        
        // Calculate category breakdown
        CategoryStatsDTO[] categoryStats = new CategoryStatsDTO[CATEGORIES.length];
        for (int i = 0; i < CATEGORIES.length; i++) {
            String category = CATEGORIES[i];
            Integer count = feedbackRepository.countByCategory(category);
            Double avgRating = feedbackRepository.getAverageRatingByCategory(category);
            
            if (avgRating == null) {
                avgRating = 0.0;
            }
            
            categoryStats[i] = new CategoryStatsDTO(category, count, avgRating);
        }
        
        return new FeedbackStatsDTO(totalFeedback, averageRating, categoryStats);
    }
}
