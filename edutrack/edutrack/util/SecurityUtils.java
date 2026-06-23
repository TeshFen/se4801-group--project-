package com.edutrack.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        // You need to store user ID in JWT or fetch from DB via email
        String email = getCurrentUserEmail();
        // Ideally, you'd decode the ID from the token directly.
        // For simplicity, we return a dummy – implement properly.
        // Better: add user ID as a claim in JWT.
        return 1L; // Replace with actual lookup
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null;
    }

    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getAuthorities().isEmpty()) {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            return role.replace("ROLE_", "");
        }
        return null;
    }
}