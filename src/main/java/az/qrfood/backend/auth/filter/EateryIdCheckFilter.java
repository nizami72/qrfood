package az.qrfood.backend.auth.filter;

import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.response.ApiResponse;
import az.qrfood.backend.common.response.ResponseCodes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Spring Web Filter that intercepts requests to check for consistency between
 * the eatery ID present in the JWT token and the eatery ID specified in the URL path.
 * <p>
 * This filter is crucial for multi-eatery environments, ensuring that a user
 * authenticated for one eatery does not inadvertently (or maliciously) access
 * resources belonging to another eatery via URL manipulation. If a mismatch is
 * detected, the request is rejected with a {@code 412 Precondition Failed} status.
 * </p>
 */
@Slf4j
public class EateryIdCheckFilter extends OncePerRequestFilter implements Ordered {

    private final JwtUtil jwtUtil;
    // Pattern to match URLs with eateryId in the path
    private static final Pattern EATERY_ID_PATTERN = Pattern.compile("/api/eatery/(\\d+)|/api/eateries/(\\d+)|/api/users/eatery/(\\d+)");

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 9;
    }

    /**
     * Constructs the EateryIdCheckFilter with a JwtUtil dependency.
     *
     * @param jwtUtil The utility for JWT token operations.
     */
    public EateryIdCheckFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Performs the internal filtering logic for each request.
     * <p>
     * This method extracts the eatery ID from the request URI and compares it
     * with the eatery ID extracted from the JWT token. If a mismatch occurs,
     * the request is terminated with an HTTP 412 (Precondition Failed) status.
     * </p>
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain to proceed with if the check passes.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException      If an I/O error occurs.
     */
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
                Long tokenEateryId;
                try {
                    tokenEateryId = jwtUtil.extractClaim(jwt, claims -> claims.get("eateryId", Long.class));
                    if(tokenEateryId == null) {
                        handleError(response, ResponseCodes.EATERY_ID_MISSING);
                        return;
                    }
                } catch (Exception e) {
                    log.error("Error extracting eateryId from JWT token", e);
                    // If a token is invalid or eateryId cannot be extracted, treat as mismatch
                    response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
                    response.getWriter().write(Util.toJsonString(new ApiResponse<Void>(ResponseCodes.INVALID_JWT, null)));
                    return;
                }

                // If eateryId is in the token and doesn't match the path's eateryId
                if (tokenEateryId != null && !tokenEateryId.equals(pathEateryId)) {
                    log.error("EateryId mismatch: token eateryId {} does not match path eateryId {}",
                            tokenEateryId, pathEateryId);

                    // In a stateless JWT system, clearing security context or redirecting
                    // is typically handled on the client side by invalidating the token.
                    // Here, we simply reject the request.
                    handleError(response, ResponseCodes.EATERY_MISMATCH);
                    SecurityContextHolder.clearContext(); // Clear context for this request
                    response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(Util.toJsonString(new ApiResponse<Void>(ResponseCodes.EATERY_MISMATCH, null)));
                    return;
                } else {
                    log.debug("EateryId matched: token eateryId {}, path eateryId {}", tokenEateryId, pathEateryId);
                }
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    private void handleError(HttpServletResponse response, ResponseCodes responseCode) throws IOException {
        SecurityContextHolder.clearContext(); // Clear context for this request
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        response.setContentType("application/json");
        response.getWriter().write(Util.toJsonString(new ApiResponse<Void>(responseCode, null)));

    }
}
