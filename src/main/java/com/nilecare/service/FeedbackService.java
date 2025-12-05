package com.nilecare.service;

import com.nilecare.dto.FeedbackPageDTO;
import com.nilecare.model.StudentFeedback;
import com.nilecare.model.User;

import java.util.List;

public interface FeedbackService {
    
    /**
     * Submit new feedback
     */
    StudentFeedback submitFeedback(User student, String category, Integer rating, String subject, String message);
    
    /**
     * Get all feedback for a student with statistics
     */
    FeedbackPageDTO getFeedbackPageData(User student);
    
    /**
     * Get feedback by ID
     */
    StudentFeedback getFeedbackById(Long feedbackId);
    
    /**
     * Update feedback status
     */
    StudentFeedback updateFeedbackStatus(Long feedbackId, StudentFeedback.FeedbackStatus status);
    
    /**
     * Add response to feedback
     */
    StudentFeedback addResponse(Long feedbackId, String response);
    
    /**
     * Delete feedback
     */
    void deleteFeedback(Long feedbackId);
    
    /**
     * Get statistics for feedback
     */
    Object getFeedbackStatistics(User student);
}
