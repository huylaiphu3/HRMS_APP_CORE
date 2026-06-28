package com.hrms.common.security;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NoArgsConstructor
public class SecurityContextUtils {
    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().map(CustomUserDetails::getUserId).orElse(null);
    }

    public static String getCurrentUserRole() {
        return getCurrentUserDetails().map(CustomUserDetails::getRole).orElse(null);
    }

    public static Long getCurrentUserDepartmentId() {
        return getCurrentUserDetails().map(CustomUserDetails::getDepartmentId).orElse(null);
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    public static boolean isManager() {
        return "MANAGER".equals(getCurrentUserRole());
    }

    public static boolean isEmployee() {
        return "EMPLOYEE".equals(getCurrentUserRole());
    }
}
