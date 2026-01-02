package com.nilecare.service;

import com.nilecare.model.User;
import com.nilecare.model.Role;

public interface UserService {
    void registerUser(User user, String rawPassword, Role.RoleType roleType);
    User loginUser(String email, String rawPassword);
    boolean emailExists(String email);
    User findByEmail(String email);
}