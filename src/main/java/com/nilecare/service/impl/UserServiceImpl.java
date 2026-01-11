package com.nilecare.service.impl;

import com.nilecare.exception.AccountDisabledException;
import com.nilecare.model.Role;
import com.nilecare.model.User;
import com.nilecare.repository.RoleRepository;
import com.nilecare.repository.UserRepository;
import com.nilecare.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional // Ensures the whole process happens or fails together
    public void registerUser(User user, String rawPassword) {
        // 1. Hash the password using Spring Security's PasswordEncoder bean
        String hashed = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(hashed);

        // 2. Assign STUDENT role (hardcoded for security - prevents privilege escalation)
        Role studentRole = roleRepository.findByName(Role.RoleType.ROLE_STUDENT);
        if (studentRole == null) {
            throw new RuntimeException("Error: STUDENT role not found in database.");
        }
        user.getRoles().add(studentRole);

        // 3. Save to DB
        userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String rawPassword) {
        // 1. Find User by Email
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 2. Check Password
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                // 3. Check if account is enabled (not deactivated)
                if (!user.isEnabled()) {
                    throw new AccountDisabledException("Your account is deactivated. Please contact support.");
                }
                return user; // Success
            }
        }
        return null; // Failed (invalid email or wrong password)
    }
    
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        Optional<User> existingUserOpt = userRepository.findById(user.getUserId());
        
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("Error: User with ID " + user.getUserId() + " not found.");
        }
        
        User existingUser = existingUserOpt.get();
        
        // Update editable fields
        existingUser.setFullName(user.getFullName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        
        // Allow email updates (for typo corrections)
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }
        
        // Update verified status (important for email change security)
        existingUser.setVerified(user.isVerified());
        
        // Save and return the updated entity
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public boolean changePassword(User user, String currentPassword, String newPassword) {
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return false; // Incorrect current password
        }
        
        // Hash the new password
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        
        // Update user's password
        user.setPasswordHash(hashedNewPassword);
        
        // Save to database
        userRepository.save(user);
        
        return true; // Success
    }

    @Override
    public List<User> findAllUsers() {
        // Return all users sorted by creation date (newest first)
        return userRepository.findAll()
            .stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .toList();
    }

    @Override
    public List<User> searchUsers(String search, String roleName) {
        // Handle empty or null search
        String keyword = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        
        // Handle role filter
        Role role = null;
        if (roleName != null && !roleName.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleName)) {
            // Find the role by name
            try {
                Role.RoleType roleType = Role.RoleType.valueOf(roleName);
                role = roleRepository.findByName(roleType);
            } catch (IllegalArgumentException e) {
                // Invalid role name, ignore and return all
                role = null;
            }
        }
        
        // Execute search with keyword and role filter
        List<User> users = userRepository.searchUsers(keyword, role);
        
        // Sort by creation date (newest first)
        return users.stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .toList();
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Error: User with ID " + userId + " not found.");
        }
        
        User user = userOpt.get();
        // Toggle the enabled status (soft delete)
        user.setEnabled(!user.isEnabled());
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createUserByAdmin(User user, String role) {
        // Set a default password for admin-created users
        String defaultPassword = "Welcome123";
        String hashed = passwordEncoder.encode(defaultPassword);
        user.setPasswordHash(hashed);

        // Parse the role string to RoleType enum
        Role.RoleType roleType;
        try {
            // Handle both "STUDENT" and "ROLE_STUDENT" formats
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            roleType = Role.RoleType.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Invalid role type: " + role);
        }

        // Assign the Role
        Role userRole = roleRepository.findByName(roleType);
        if (userRole == null) {
            throw new RuntimeException("Error: Role " + roleType + " not found.");
        }
        user.getRoles().add(userRole);
        
        // Set user as enabled by default
        user.setEnabled(true);

        // Save to DB
        userRepository.save(user);
    }

    @Override
    public User findById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Error: User with ID " + userId + " not found.");
        }
        
        User user = userOpt.get();
        
        // Parse the role string to RoleType enum
        Role.RoleType roleType;
        try {
            // Handle both "STUDENT" and "ROLE_STUDENT" formats
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            roleType = Role.RoleType.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Invalid role type: " + role);
        }

        // Find the new role
        Role newRole = roleRepository.findByName(roleType);
        if (newRole == null) {
            throw new RuntimeException("Error: Role " + roleType + " not found.");
        }

        // Clear existing roles and assign new role
        user.getRoles().clear();
        user.getRoles().add(newRole);
        
        // Save to DB
        userRepository.save(user);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public void verifyUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Error: User with ID " + userId + " not found.");
        }
        
        User user = userOpt.get();
        user.setVerified(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Error: User with email " + email + " not found.");
        }
        
        User user = userOpt.get();
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public long countPendingVerifications() {
        return userRepository.countByVerifiedFalse();
    }

    @Override
    public long countActiveStudents() {
        return userRepository.countStudents();
    }

    @Override
    public List<User> getRecentlyRegisteredUsers() {
        return userRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public long countUsersBefore(LocalDateTime date) {
        return userRepository.countByCreatedAtBefore(date);
    }
}