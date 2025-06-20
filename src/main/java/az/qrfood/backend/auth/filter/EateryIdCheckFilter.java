package az.qrfood.backend.auth.filter;

import az.qrfood.backend.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter that checks if the eateryId in the JWT token matches the eateryId in the URL path.
 * If they don't match, the user is logged out and redirected to the login page.
 */
@Component
@Slf4j
public class EateryIdCheckFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    // Pattern to match URLs with eateryId in the path
    private static final Pattern EATERY_ID_PATTERN = Pattern.compile("/api/eatery/(\\d+)|/api/eateries/(\\d+)|/api/users/eatery/(\\d+)");

    public EateryIdCheckFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        Matcher matcher = EATERY_ID_PATTERN.matcher(path);

        // Only check paths that contain eateryId
        if (matcher.find()) {
            // Extract eateryId from URL path
            String eateryIdStr = null;
            if (matcher.group(1) != null) {
                eateryIdStr = matcher.group(1);
            } else if (matcher.group(2) != null) {
                eateryIdStr = matcher.group(2);
            } else if (matcher.group(3) != null) {
                eateryIdStr = matcher.group(3);
            }

            if (eateryIdStr == null) {
                // If we couldn't extract the eateryId, continue with the filter chain
                filterChain.doFilter(request, response);
                return;
            }

            Long pathEateryId = Long.parseLong(eateryIdStr);

            // Get JWT token from the Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwt = authorizationHeader.substring(7);

                // Extract eateryId from JWT token
                Long tokenEateryId = null;
                try {
                    tokenEateryId = jwtUtil.extractClaim(jwt, claims -> claims.get("eateryId", Long.class));
                } catch (Exception e) {
                    log.error("Error extracting eateryId from JWT token", e);
                }

                // todo If eateryId is in the token and doesn't match the path's eateryId then logout and redirect
                if (tokenEateryId != null && !tokenEateryId.equals(pathEateryId)) {
                    log.error("EateryId mismatch: token eateryId {} does not match path eateryId {}", 
                              tokenEateryId, pathEateryId);

                    // todo Clear authentication doesnt work
                    SecurityContextHolder.clearContext();

                    // todo Redirect to login page doesnt work
                    response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
                    response.getWriter().write("EateryId mismatch");
                    return;
                }
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
