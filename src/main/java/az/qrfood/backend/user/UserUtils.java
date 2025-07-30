package az.qrfood.backend.user;

import az.qrfood.backend.user.entity.Role;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class UserUtils {

    private static final List<Role> ROLE_PRIORITY = List.of(
            Role.SUPER_ADMIN,
            Role.EATERY_ADMIN,
            Role.KITCHEN_ADMIN,
            Role.CASHIER,
            Role.WAITER
    );


    public static int compareRoles(Set<Role> roleOfUserUnderChange) {
        Optional<Role> opCurrentUserRole = getHighestRole(getCurrentUserRoles());
        Optional<Role> opRoleOfUserUnderChange = getHighestRole(roleOfUserUnderChange);
        if (opCurrentUserRole.isEmpty() || opRoleOfUserUnderChange.isEmpty()) {
            throw new RuntimeException("Unknown role");
        }
        return compareRoles(opCurrentUserRole.get(), opRoleOfUserUnderChange.get());
    }

    public static Set<Role> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return Set.of();
        }
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleStr -> Role.valueOf(roleStr.replace("ROLE_", "")))
                .collect(Collectors.toSet());
    }

    public static Optional<Role> getHighestRole(Set<Role> roles) {
        for (Role role : ROLE_PRIORITY) {
            if (roles.contains(role)) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }

    /**
     * Compare two roles based on predefined priority.
     *
     * @return -1 if roleOfUserUnderChange > currentRole, 1 if currentRole > roleOfUserUnderChange, 0 if equal or both not found
     */
    public static int compareRoles(Role currentRole, Role roleOfUserUnderChange) {
        log.debug("CurrentRole [{}]", currentRole);
        log.debug("RoleOfUserUnderChange [{}]", roleOfUserUnderChange);
        int indexOfUserUnderChange = ROLE_PRIORITY.indexOf(roleOfUserUnderChange);
        int indexOfCurrentRole = ROLE_PRIORITY.indexOf(currentRole);

        if (indexOfUserUnderChange == -1 && indexOfCurrentRole == -1) return 0;
        if (indexOfUserUnderChange == -1) return 1;  // roleOfUserUnderChange unknown → lower priority
        if (indexOfCurrentRole == -1) return -1; // currentRole unknown → lower priority

        return Integer.compare(indexOfCurrentRole, indexOfUserUnderChange);
    }

    public static void deleteSuperUserRole(Set<Role> roles) {
        roles.remove(Role.SUPER_ADMIN);
    }


}