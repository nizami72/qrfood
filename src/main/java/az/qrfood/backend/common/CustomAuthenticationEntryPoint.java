package az.qrfood.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Custom implementation of Spring Security's {@link AuthenticationEntryPoint}.
 * <p>
 * This class is responsible for handling unauthorized access attempts. When an
 * unauthenticated user tries to access a secured resource, this entry point
 * is invoked to send an appropriate error response to the client.
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences an authentication scheme.
     * <p>
     * This method is called when an unauthenticated user attempts to access a secured resource.
     * It sets the HTTP status to 401 (Unauthorized) and sends a JSON error message to the client.
     * </p>
     *
     * @param request       The {@link HttpServletRequest} that resulted in an {@link AuthenticationException}.
     * @param response      The {@link HttpServletResponse} to send the error response.
     * @param authException The {@link AuthenticationException} that caused the commencement.
     * @throws IOException If an I/O error occurs during the response writing.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Need Authorisation\"}");
    }
}
