package az.qrfood.backend.util;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
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

    public static <T> T json2Pojo(String jsoString, Class<?> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object pojo = objectMapper.readValue(jsoString, clazz);
        return pojo == null ? null : (T) pojo;
    }

    public static String readFileFromResources(String fileName) {
        try (InputStream inputStream = az.qrfood.backend.common.Util.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Error reading resource file [{}]", e.getMessage());
            return null;
        }
    }

}