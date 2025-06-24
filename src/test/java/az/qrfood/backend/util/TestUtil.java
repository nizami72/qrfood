package az.qrfood.backend.util;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import org.springframework.security.core.userdetails.User;
import java.util.Set;

public class TestUtil {

    public static RegisterRequest createRegisterRequest() {
        String name = FakeData.user(10);
        String mail = FakeData.mail(name);
        String eateryName = FakeData.eateryName();
        String phone = FakeData.phones().get(0);
        String password = mail.split("@")[0] + FakeData.getRandomInt(1111, 9999);
        return RegisterRequest.builder()
                .user(RegisterRequest.UserDto.builder()
                        .email(mail)
                        .password(password)
                        .roles(Set.of(Role.fromString("EATERY_ADMIN")))
                        .build())
                .restaurant(RegisterRequest.RestaurantDto.builder()
                        .name(eateryName)

                        .build())
                .userProfileRequest(RegisterRequest.UserProfileRequest.builder()
                        .name(name)
                        .phone(phone)
                        .build())
                .build();

    }

    public static RegisterRequest createRegisterRequest(Set<Role> role) {
        RegisterRequest r = createRegisterRequest();
        r.getUser().setRoles(role);
        return r;
    }
}