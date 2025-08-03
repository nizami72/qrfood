package az.qrfood.backend.auth.filter;

import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Filter for processing JWT tokens in each request.
 * <p>
 * This filter intercepts incoming HTTP requests, extracts the JWT from the
 * Authorization header, validates it, and sets the authentication in the
 * Spring SecurityContext if the token is valid.
 * </p>
 */
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter implements Ordered {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final List<PathPattern> excluded = Stream.of(
                    "/api/auth/**",
                    "/api/image/**",
                    "/api/client/**",
                    "/api/logs/frontend",
                    "/api/config/image-paths",
                    "/ui/alive",
                    "/api/eatery/{eateryId}/order/{orderId}",
                    "/api/eatery/{eateryId}/order/status/created",
                    "/api/eatery/{eateryId}/order"
            )
            .map(pattern -> new PathPatternParser().parse(pattern))
            .toList();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return excluded.stream().anyMatch(pattern -> pattern.matches(PathContainer.parsePath(path)));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    /**
     * Constructs the JwtRequestFilter with necessary dependencies.
     *
     * @param userDetailsService The custom user details service for loading user data.
     * @param jwtUtil            The utility for JWT token operations.
     */
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Performs the internal filtering logic for each request.
     * <p>
     * This method extracts the JWT from the request, validates it, and if valid,
     * sets up the authentication in the {@link SecurityContextHolder}.
     * </p>
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain to proceed with.
     * @throws ServletException If a servlet-related error occurs.
     * @throws IOException      If an I/O error occurs.
     */

    /**
     * Sends an error response to the client.
     *
     * @param response The HTTP response.
     * @param status The HTTP status code.
     * @param message The error message.
     * @throws IOException If an I/O error occurs.
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorDetails);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI(); // e.g., /api/client/whatever

//        // Skip filter logic if the path starts with unneeded
//        for (String pathExcluded : excludedPaths) {
//            if(path.startsWith(pathExcluded)) {
//                filterChain.doFilter(request, response); // continue without filter logic
//                return;
//            }
//        }

        log.debug("JwtRequestFilter.doFilterInternal");
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check for the presence and format of the Authorization header (must start with "Bearer ")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract the token itself
            try {
                username = jwtUtil.extractUsername(jwt); // Extract the username from the token
            } catch (ExpiredJwtException e) {
                log.warn("JWT token expired for path: {}", path);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token has expired");
                return;
            } catch (MalformedJwtException | UnsupportedJwtException | SignatureException e) {
                log.warn("Invalid JWT token for path: {}", path);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
                return;
            } catch (Exception e) {
                log.error("Error processing JWT token for path: {}", path, e);
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JWT token");
                return;
            }
        }

        // If a username is extracted and the current SecurityContext does not contain authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validate the token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // If the token is valid, create an authentication object
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Set authentication details from the HTTP request
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication object in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (Exception e) {
                log.error("Error authenticating user for path: {}", path, e);
                sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Error authenticating user");
                return;
            }
        }

        // Pass the request further down the filter chain
        filterChain.doFilter(request, response);
    }
}
