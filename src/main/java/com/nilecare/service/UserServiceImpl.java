package com.nilecare.service;

import com.nilecare.model.Role;
import com.nilecare.model.User;
import com.nilecare.repository.RoleRepository;
import com.nilecare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void registerUser(User user, String rawPassword, Role.RoleType roleType) {
        // 1. Hash the password using Spring Security's PasswordEncoder bean
        String hashed = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(hashed);

        // 2. Assign the Role
        Role userRole = roleRepository.findByName(roleType);
        if (userRole == null) {
            throw new RuntimeException("Error: Role " + roleType + " not found.");
        }
        user.getRoles().add(userRole);

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
                return user; // Success
            }
        }
        return null; // Failed
    }
    
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}