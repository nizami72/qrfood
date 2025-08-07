package az.qrfood.backend.table.interceptor;

import az.qrfood.backend.dish.interceptor.NotYourResourceException;
import az.qrfood.backend.table.service.TableService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Interceptor to validate that eateryId and tableId in requests belong to each other.
 * <p>
 * This interceptor checks requests that contain both eateryId and tableId path variables
 * and ensures that the table actually belongs to the specified eatery.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class EateryTableRelationshipInterceptor implements HandlerInterceptor {

    private final TableService tableService;

    /**
     * Pre-handle method to validate the relationship between eatery and table.
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

        if (pathVariables != null && pathVariables.containsKey("eateryId") && pathVariables.containsKey("tableId")) {
            try {
                Long eateryId = Long.parseLong(pathVariables.get("eateryId"));
                Long tableId = Long.parseLong(pathVariables.get("tableId"));

                log.debug("Checking if table [{}] belongs to eatery [{}]", tableId, eateryId);
                if (!tableService.isTableBelongsToEatery(tableId, eateryId)) {
                    log.warn("Table [{}] does not belong to eatery [{}]", tableId, eateryId);
                    throw new NotYourResourceException("Access to resources that are not belong to each other or does not exist: " + tableId);
                }
            } catch (NumberFormatException e) {
                log.error("Failed to parse eateryId or tableId from path variables", e);
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid eatery or table ID");
                return false;
            }
        }

        return true;
    }
}
