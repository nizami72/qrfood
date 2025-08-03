package az.qrfood.backend.auth.filter;

import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        jwtRequestFilter = new JwtRequestFilter(userDetailsService, jwtUtil);
    }

    @Test
    void shouldReturnErrorResponseWhenTokenExpired() throws Exception {
        // Given
        String testPath = "/api/some/protected/endpoint";
        String expiredToken = "expired.jwt.token";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        };

        when(request.getRequestURI()).thenReturn(testPath);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtUtil.extractUsername(expiredToken)).thenThrow(new ExpiredJwtException(null, null, "JWT expired"));
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Verify the response contains the expected error message
        String responseBody = outputStream.toString();
        assertTrue(responseBody.contains("JWT token has expired"), "Response should contain expired token message");

        // Verify that filterChain.doFilter was not called
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldContinueFilterChainWhenTokenValid() throws ServletException, IOException {
        // Given
        String testPath = "/api/some/protected/endpoint";
        String validToken = "valid.jwt.token";
        String username = "testuser";

        when(request.getRequestURI()).thenReturn(testPath);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails)).thenReturn(true);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        // Verify that filterChain.doFilter was called
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipFilterForExcludedPaths() {
        // Given
        String excludedPath = "/api/auth/login";

        when(request.getRequestURI()).thenReturn(excludedPath);

        // When
        boolean shouldNotFilter = jwtRequestFilter.shouldNotFilter(request);

        // Then
        assertTrue(shouldNotFilter, "Filter should be skipped for excluded paths");
    }
}
