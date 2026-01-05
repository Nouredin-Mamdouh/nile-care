package com.nilecare.repository;

import com.nilecare.model.Role;
import com.nilecare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // For Login
    
    /**
     * Search users by keyword (name or email) and optionally filter by role
     * @param keyword Search term for name or email (case insensitive)
     * @param role Role to filter by (null for all roles)
     * @return List of matching users
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE " +
           "(:role IS NULL OR r = :role) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("role") Role role);
    
    /**
     * Count users who are not verified
     * @return Count of unverified users
     */
    long countByVerifiedFalse();
    
    /**
     * Count users with a specific role
     * @return Count of users with the role ROLE_STUDENT
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.name = 'ROLE_STUDENT'")
    long countStudents();
    
    /**
     * Find the 5 most recently registered users
     * @return List of top 5 users ordered by creation date descending
     */
    List<User> findTop5ByOrderByCreatedAtDesc();
    
    /**
     * Count users created before a specific date
     * @param date The date to check against
     * @return Count of users created before the specified date
     */
    long countByCreatedAtBefore(LocalDateTime date);

    /**
     * Count users created within a date range
     * @param startDate The start of the date range (inclusive)
     * @param endDate The end of the date range (inclusive)
     * @return Count of users created between startDate and endDate
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}