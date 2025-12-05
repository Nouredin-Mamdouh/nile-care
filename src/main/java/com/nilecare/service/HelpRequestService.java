package com.nilecare.service;

import com.nilecare.dto.HelpRequestDTO;
import com.nilecare.model.HelpRequest;
import com.nilecare.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface HelpRequestService {
    
    /**
     * Submit a new help request
     */
    HelpRequestDTO submitHelpRequest(Long userId, String category, String subject, String message);
    
    /**
     * Get all help requests for a student
     */
    List<HelpRequestDTO> getHelpRequests(Long userId);
    
    /**
     * Get help requests by status
     */
    List<HelpRequestDTO> getHelpRequestsByStatus(Long userId, String status);
    
    /**
     * Get a single help request
     */
    HelpRequestDTO getHelpRequest(Long requestId);
    
    /**
     * Update help request status and response (admin only)
     */
    HelpRequestDTO updateHelpRequest(Long requestId, String status, String response);
    
    /**
     * Get count of help requests by status
     */
    long getRequestCountByStatus(Long userId, String status);
    
    /**
     * Get resolution rate for a student
     */
    double getResolutionRate(Long userId);
}
