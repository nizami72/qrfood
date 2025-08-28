package az.qrfood.backend.auth;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Component for checking user permissions and roles within the application.
 * <p>
 * This class provides utility methods to verify if an authenticated user
 * has specific roles or permissions, often used with Spring Security's
 * {@code @PreAuthorize} annotations.
 * </p>
 */
@Component("authz")
@Log4j2
public class PermissionChecker {

    /**
     * Checks if the authenticated user has the 'SUPER_ADMIN' role.
     *
     * @param auth The current {@link Authentication} object.
     * @return {@code true} if the user is a super admin, {@code false} otherwise.
     */
    public boolean isSuperAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("SUPER_ADMIN"));
    }

    /**
     * Checks if the authenticated user has any of the specified roles.
     * <p>
     * If the user is a 'SUPER_ADMIN', this method immediately returns {@code true},
     * bypassing further role checks.
     * </p>
     *
     * @param auth  The current {@link Authentication} object.
     * @param roles A variable number of role names (e.g., "EATERY_ADMIN", "WAITER").
     * @return {@code true} if the user has any of the specified roles or is a super admin,
     *         {@code false} otherwise.
     */
    public boolean hasAnyRole(Authentication auth, String... roles) {
        if (isSuperAdmin(auth)) return true;

        for (String role : roles) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role))) {
                return true;
            }
        }
        return false;
    }
}
