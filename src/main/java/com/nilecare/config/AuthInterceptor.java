package com.nilecare.config;

import com.nilecare.model.User;
import com.nilecare.model.Role;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String uri = request.getRequestURI();
        
        // 1. Allow Public Pages
        if (uri.endsWith("/login") || 
            uri.endsWith("/register") || 
            uri.contains("/static/") || 
            uri.equals(request.getContextPath() + "/")) {
            return true;
        }

        // 2. Check if User is Logged In
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser"); // We will save this on login

        if (user == null) {
            // For API requests, return 401 Unauthorized instead of redirect
            if (uri.contains("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"User not authenticated\"}");
                return false;
            }
            
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // 3. Role-Based Access Control (RBAC)
        if (uri.startsWith(request.getContextPath() + "/admin") && !hasRole(user, "ROLE_ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return false;
        }

        if (uri.startsWith(request.getContextPath() + "/counselor") && !hasRole(user, "ROLE_COUNSELOR")) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return false;
        }

        return true; // Allow access
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                   .anyMatch(r -> r.getName().name().equals(roleName));
    }
}