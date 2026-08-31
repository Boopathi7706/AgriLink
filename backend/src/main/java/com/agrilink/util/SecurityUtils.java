package com.agrilink.util;

import com.agrilink.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing authenticated user details from SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        throw new AccessDeniedException("User is not authenticated");
    }

    public static Long getCurrentUserId() {
        return getCurrentPrincipal().getId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentPrincipal().getEmail();
    }
}
