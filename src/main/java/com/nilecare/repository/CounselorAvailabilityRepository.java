package com.nilecare.repository;

import com.nilecare.model.CounselorAvailability;
import com.nilecare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CounselorAvailabilityRepository extends JpaRepository<CounselorAvailability, Long> {
    // Find a specific slot for a counselor at a specific time
    Optional<CounselorAvailability> findByCounselorAndStartTime(User counselor, LocalDateTime startTime);
}