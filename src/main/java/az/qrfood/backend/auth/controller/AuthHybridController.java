package az.qrfood.backend.auth.controller;

import az.qrfood.backend.auth.dto.GoogleLoginRequest;
import az.qrfood.backend.auth.dto.LoginResponse;
import az.qrfood.backend.auth.dto.MagicLinkRequest;
import az.qrfood.backend.auth.dto.VerifyTokenRequest;
import az.qrfood.backend.auth.service.AuthHybridService;
import az.qrfood.backend.constant.ApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@Tag(name = "HybridAuth", description = "Magic link and Google OAuth endpoints")
public class AuthHybridController {

    private final AuthHybridService authHybridService;
    private String magicLink;

    @PostMapping(ApiRoutes.AUTH_MAGIC_LINK)
    @Operation(summary = "Request a magic login link")
    public ResponseEntity<?> createAndSendMagicLink(HttpServletRequest request1,
                                                    @RequestBody MagicLinkRequest request, // 1. Для IP
                                                    @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        magicLink = authHybridService.createMicLinkAndPublishEvent(request.getEmail(), request1.getRemoteAddr(), userAgent);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PreAuthorize("@authz.isSuperAdmin(authentication)")
    @GetMapping(ApiRoutes.AUTH_TEST_MAGIC_LINK)
    @Operation(summary = "Request a magic login link for selenium test only")
    public String getMagikLink() {
        return magicLink;
    }

    /*
     * Verify magic link token and login
     */
    @PostMapping(ApiRoutes.AUTH_VERIFY_TOKEN)
    @Operation(summary = "Verify magic link token and login")
    public ResponseEntity<LoginResponse> verifyToken(@RequestBody VerifyTokenRequest request, HttpServletResponse response) {
        LoginResponse response1 = authHybridService.verifyToken(request.getToken(), response);
        return ResponseEntity.ok(response1);
    }

    @PostMapping(ApiRoutes.AUTH_OAUTH_GOOGLE)
    @Operation(summary = "Process Google OAuth login (stub)")
    public ResponseEntity<LoginResponse> google(@RequestBody GoogleLoginRequest request, HttpServletResponse response) {
        LoginResponse response1 = authHybridService.processGoogleLogin(request.getToken(), response);
        return ResponseEntity.ok(response1);
    }

    @PostMapping(ApiRoutes.AUTH_PASSWORD_RESET_REQUEST)
    @Operation(summary = "Request password reset link")
    public ResponseEntity<?> passwordResetRequest(HttpServletRequest request1,
                                                  @RequestBody PasswordResetRequest request,
                                                  @RequestHeader(value = "User-Agent", required = false) String userAgent) {

        authHybridService.requestPasswordReset(request.getEmail(),request1.getRemoteAddr(),  userAgent);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping(ApiRoutes.AUTH_PASSWORD_RESET_COMPLETE)
    @Operation(summary = "Complete password reset with token")
    public ResponseEntity<?> passwordResetComplete(@RequestBody PasswordResetCompleteRequest request) {
        authHybridService.completePasswordReset(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("success", true));
    }

    public static class PasswordResetRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class PasswordResetCompleteRequest {
        private String token;
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
