package com.nilecare.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Extended UserDetails implementation that includes full name
 * and provides a method to get user initials for avatar display
 */
public class CustomUserDetails extends User {

    private final String fullName;

    public CustomUserDetails(com.nilecare.model.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPasswordHash(), authorities);
        this.fullName = user.getFullName();
    }

    /**
     * Get user initials from full name (e.g., "John Doe" -> "JD")
     * @return uppercase initials, or first letter if only one name, or empty string if null
     */
    public String getInitials() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        String trimmed = fullName.trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length == 0) {
            return "";
        } else if (parts.length == 1) {
            // Single name: return first letter
            return parts[0].substring(0, Math.min(1, parts[0].length())).toUpperCase();
        } else {
            // Multiple names: return first letter of first and last name
            String first = parts[0].substring(0, Math.min(1, parts[0].length()));
            String last = parts[parts.length - 1].substring(0, Math.min(1, parts[parts.length - 1].length()));
            return (first + last).toUpperCase();
        }
    }

    public String getFullName() {
        return fullName;
    }
}
