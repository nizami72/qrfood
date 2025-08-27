package az.qrfood.backend.user.interceptor;

import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;

/**
 * Interceptor to validate that eateryId and userId in requests belong to each other.
 * <p>
 * This interceptor checks requests that contain both eateryId and userId path variables
 * and ensures that the user actually belongs to the specified eatery.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class UserControllerInterceptor implements HandlerInterceptor {

    private final UserService userService;

    // URI variables injected from application.properties
    @Value("${user.id}")
    private String userIdPath;

    /**
     * Pre-handle method to validate the relationship between eatery and user.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param handler  The handler for the request
     * @return true if the request should proceed, false otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables != null && pathVariables.containsKey("eateryId") && pathVariables.containsKey("userId")) {
            try {
                Long eateryId = Long.parseLong(pathVariables.get("eateryId"));
                Long userId = Long.parseLong(pathVariables.get("userId"));

                log.debug("Checking if user [{}] belongs to eatery [{}]", userId, eateryId);
                try {
                    userService.getUserById(eateryId, userId);
                } catch (EntityNotFoundException e) {
                    log.warn("User [{}] does not belong to eatery [{}]", userId, eateryId);
                    throw new NotYourResourceException("Access to resources that do not belong to each other or do not exist: " + userId);
                }
            } catch (NumberFormatException e) {
                log.error("Failed to parse eateryId or userId from path variables", e);
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid eatery or user ID");
                return false;
            }
        }

        return true;
    }
}
