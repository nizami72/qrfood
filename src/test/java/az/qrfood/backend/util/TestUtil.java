package az.qrfood.backend.util;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import java.util.Set;

public class TestUtil {

    /**
     * The first user always John Kimber
     *
     * @param first if true, first is created. random another way
     * @return RegisterRequest
     */
    public static RegisterRequest createRegisterRequest(boolean first) {
        String name = FakeData.user(10);
        if(first) {
            name = "John Kimber";
        }
        String mail = FakeData.mail(name);
        String eateryName = FakeData.eateryName();
        String phone = FakeData.phones().get(0);
//        String password = mail.split("@")[0] + FakeData.getRandomInt(1111, 9999);
        String password = "qqqq1111";
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

    public static RegisterRequest createRegisterRequest(Set<Role> role, boolean first) {
        RegisterRequest r = createRegisterRequest(first);
        r.getUser().setRoles(role);
        return r;
    }
}