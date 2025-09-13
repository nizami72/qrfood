package az.qrfood.backend.util;

import az.qrfood.backend.user.entity.Role;
import lombok.Builder;
import java.util.Set;

@Builder
public record UserAndProfile(String mail, String password, String name, String phone, Set<Role> roles) {
}
