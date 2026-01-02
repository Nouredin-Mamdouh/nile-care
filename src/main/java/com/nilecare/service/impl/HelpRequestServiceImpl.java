package com.nilecare.service.impl;

import com.nilecare.dto.HelpRequestDTO;
import com.nilecare.model.HelpRequest;
import com.nilecare.model.User;
import com.nilecare.repository.HelpRequestRepository;
import com.nilecare.repository.UserRepository;
import com.nilecare.service.HelpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HelpRequestServiceImpl implements HelpRequestService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private HelpRequestRepository helpRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public HelpRequestDTO submitHelpRequest(Long userId, String category, String subject, String message) {
        User student = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setStudent(student);
        helpRequest.setCategory(category);
        helpRequest.setSubject(subject);
        helpRequest.setMessage(message);
        helpRequest.setStatus(HelpRequest.RequestStatus.PENDING);

        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);
        return convertToDTO(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getHelpRequests(Long userId) {
        User student = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<HelpRequest> requests = helpRequestRepository.findByStudentOrderByCreatedAtDesc(student);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getHelpRequestsByStatus(Long userId, String status) {
        User student = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HelpRequest.RequestStatus requestStatus = HelpRequest.RequestStatus.valueOf(status.toUpperCase());
        List<HelpRequest> requests = helpRequestRepository.findByStudentAndStatusOrderByCreatedAtDesc(student, requestStatus);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HelpRequestDTO getHelpRequest(Long requestId) {
        HelpRequest request = helpRequestRepository.findById((Long) requestId)
                .orElseThrow(() -> new RuntimeException("Help request not found"));
        return convertToDTO(request);
    }

    @Override
    public HelpRequestDTO updateHelpRequest(Long requestId, String status, String response) {
        HelpRequest request = helpRequestRepository.findById((Long) requestId)
                .orElseThrow(() -> new RuntimeException("Help request not found"));

        request.setStatus(HelpRequest.RequestStatus.valueOf(status.toUpperCase()));
        request.setResponse(response);

        HelpRequest updatedRequest = helpRequestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public long getRequestCountByStatus(Long userId, String status) {
        User student = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HelpRequest.RequestStatus requestStatus = HelpRequest.RequestStatus.valueOf(status.toUpperCase());
        return helpRequestRepository.countByStudentAndStatus(student, requestStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public double getResolutionRate(Long userId) {
        User student = userRepository.findById((Long) userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double rate = helpRequestRepository.getResolutionRateForStudent(student, HelpRequest.RequestStatus.RESOLVED);
        return rate != null ? rate : 0.0;
    }

    private HelpRequestDTO convertToDTO(HelpRequest request) {
        return new HelpRequestDTO(
                request.getRequestId(),
                request.getCategory(),
                request.getSubject(),
                request.getMessage(),
                request.getStatus().toString(),
                request.getResponse(),
                request.getCreatedAt() != null ? request.getCreatedAt().format(DATE_FORMATTER) : null,
                request.getUpdatedAt() != null ? request.getUpdatedAt().format(DATE_FORMATTER) : null
        );
    }
}
