package com.nilecare.service;

import com.nilecare.model.User;
import com.nilecare.model.Role;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    void registerUser(User user, String rawPassword);
    User loginUser(String email, String rawPassword);
    boolean emailExists(String email);
    User findByEmail(String email);
    User updateUser(User user);
    boolean changePassword(User user, String currentPassword, String newPassword);
    
    // Admin user management methods
    List<User> findAllUsers();
    List<User> searchUsers(String search, String roleName);
    void toggleUserStatus(Long userId);
    void createUserByAdmin(User user, String role);
    User findById(Long userId);
    void updateUserRole(Long userId, String role);
    long countUsers();
    void verifyUser(Long userId);
    void deactivateUser(String email);
    
    // Dashboard stats methods
    long countPendingVerifications();
    long countActiveStudents();
    List<User> getRecentlyRegisteredUsers();
    long countUsersBefore(LocalDateTime date);
}