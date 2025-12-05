package com.nilecare.repository;

import com.nilecare.model.HelpRequest;
import com.nilecare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {
    
    List<HelpRequest> findByStudentOrderByCreatedAtDesc(User student);
    
    List<HelpRequest> findByStudentAndStatusOrderByCreatedAtDesc(User student, HelpRequest.RequestStatus status);
    
    @Query("SELECT COUNT(hr) FROM HelpRequest hr WHERE hr.student = :student AND hr.status = :status")
    long countByStudentAndStatus(@Param("student") User student, @Param("status") HelpRequest.RequestStatus status);
    
    @Query("SELECT AVG(CASE WHEN hr.status = :resolvedStatus THEN 1.0 ELSE 0.0 END) FROM HelpRequest hr WHERE hr.student = :student")
    Double getResolutionRateForStudent(@Param("student") User student, @Param("resolvedStatus") HelpRequest.RequestStatus resolvedStatus);
}
