package az.qrfood.backend.auth;

import az.qrfood.backend.user.entity.UserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
public class PermissionChecker {

    public boolean isSuperAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public boolean hasAnyRole(Authentication auth, String... roles) {
        if (isSuperAdmin(auth)) return true;

        for (String role : roles) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role))) {
                return true;
            }
        }
        return false;
    }


//    public boolean hasEateryAccess(Authentication auth, Long eateryId) {
//        if (auth == null || !auth.isAuthenticated()) return false;
//
//        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) return true;
//
//        UserProfile userProfile = auth.getPrincipal();
//        return userProfile.getEateryIds().contains(eateryId);
//    }

}
