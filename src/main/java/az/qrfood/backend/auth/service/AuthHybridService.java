package az.qrfood.backend.auth.service;

import static az.qrfood.backend.auth.util.Util.sha256;

import az.qrfood.backend.auth.dto.LoginResponse;
import az.qrfood.backend.auth.entity.AuthToken;
import az.qrfood.backend.auth.entity.AuthToken.TokenType;
import az.qrfood.backend.auth.entity.RefreshToken;
import az.qrfood.backend.auth.exception.TokenException;
import az.qrfood.backend.auth.repository.AuthTokenRepository;
import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.mail.EventPublisherHelper;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserProfileRepository;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthHybridService {

    //<editor-fold desc="Fields" defaultstate="collapsed">
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    @Value("${host.name.redirect}")
    private String frontendBaseUrl;
    @Value("${default.locale}")
    private String defaultLocale;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final RefreshTokenService refreshTokenService;
    private final AuthTokenService authTokenService;
    private final EventPublisherHelper eventPublisherHelper;
    //</editor-fold>

    @Transactional
    public String createMicLinkAndPublishEvent(String email, String ipAddress, String userAgent) {
        Optional<User> userOpt = userRepository.findByUsername(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserProfile profile = user.getProfile();
            String name = (profile != null && profile.getName() != null) ? profile.getName() : email;
            String locale = (profile != null && profile.getLocale() != null) ? profile.getLocale() : defaultLocale;
            return eventPublisherHelper.createMicLinkAndPublishEvent(user, name, email, ipAddress, userAgent, locale);
        }
        userService.createUserAndProfile(email, null);
        return null;
    }

    @Transactional
    public LoginResponse verifyToken(String token, HttpServletResponse response) {
        String tokenHash = sha256(token);
        AuthToken authToken = authTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenException("Token not found"));
        if (authToken.getExpiryDate().isBefore(Instant.now())) {
            authTokenRepository.delete(authToken);
            throw new TokenException("Token expired or invalid");
        }
        User user = authToken.getUser();
        authTokenRepository.delete(authToken);
        user.getProfile().setLastLogin(LocalDateTime.now());
        return createLoginResponse(user, null, response);
    }

    @Transactional
    public LoginResponse processGoogleLogin(String googleIdTokenString, HttpServletResponse response) {

        // ✨ 2. ПРОВЕРКА (ВЕРИФИКАЦИЯ) - Самый главный шаг
        // (Мы вынесли это в отдельный метод для "чистоты")
        GoogleIdToken.Payload payload = verifyGoogleToken(googleIdTokenString);

        // ✨ 3. "ЛОГИКА UPSERT" (Найти или Создать)
        // Мы ищем или создаем пользователя, используя *проверенные* данные из payload
        User user = findOrCreateGoogleUser(payload);

        // ✨ 4. Generating JWT and refresh token
        return createLoginResponse(user, null, response);
    }

    /**
     * Вспомогательный метод для верификации токена.
     */
    private GoogleIdToken.Payload verifyGoogleToken(String googleIdTokenString) {

        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(googleIdTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("todo Error verifying Google ID Token", e);// todo add exception handling
        }

        if (idToken == null) {
            throw new RuntimeException("todo Invalid Google ID Token."); // todo add exception handling
        }
        return idToken.getPayload();
    }

    /**
     * Finds or creates a Google user.
     */
    private User findOrCreateGoogleUser(GoogleIdToken.Payload payload) {

        // 1. ПОЛУЧАЕМ "НАСТОЯЩИЕ" ДАННЫЕ
        String googleId = payload.getSubject(); // Это 100% уникальный ID пользователя
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // 2. ИЩЕМ ПО GOOGLE ID
        // (Это самый быстрый "флоу" - обычный "вход")
        Optional<User> userByGoogleId = userRepository.findByGoogleId(googleId);
        if (userByGoogleId.isPresent()) {
            User existingUser = userByGoogleId.get();
            userProfileRepository.findByUser(existingUser).orElseThrow().setLastLogin(LocalDateTime.now());
            return existingUser; // <-- Пользователь найден, возвращаем
        }

        // 3. ИЩЕМ ПО EMAIL
        // (Сценарий: "Пользователь (с паролем) уже есть, но "логинится" через Google в 1-й раз")
        Optional<User> userByEmail = userRepository.findByUsername(email);
        if (userByEmail.isPresent()) {
            User existingUser = userByEmail.get();
            UserProfile userProfile = userProfileRepository.findByUser(existingUser).orElseThrow();
            // "Связываем" (link) его аккаунт с Google ID
            existingUser.setGoogleId(googleId);
            userProfile.setName(name); // (Обновляем имя, если нужно)
            userProfile.setLastLogin(LocalDateTime.now());
            return userRepository.save(existingUser); // <-- Сохраняем "связку"
        }

        // 4. НОВЫЙ ПОЛЬЗОВАТЕЛЬ
        // (Не найден ни по Google ID, ни по Email - это "чистая" регистрация)
        return userService.createUserAndProfile(email, null);

    }

    @Transactional
    public void requestPasswordReset(String email, String ipAddress, String userAgent) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = authTokenService.createMagicLinkToken(user);
        String link = String.format("%s/auth/verify?token=%s", frontendBaseUrl, token);

        // todo publish event
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        String tokenHash = sha256(token);
        AuthToken authToken = authTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        if (authToken.getExpiryDate().isBefore(Instant.now()) || authToken.getTokenType() != TokenType.PASSWORD_RESET) {
            authTokenRepository.delete(authToken);
            throw new IllegalArgumentException("Token invalid");
        }
        User user = authToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        authTokenRepository.delete(authToken);
    }


    public LoginResponse createLoginResponse(User user, Long eateryId, HttpServletResponse response) {

        log.debug("Generating JWT Token with eateryId: {}", eateryId);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails, eateryId);

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, eateryId);

        log.debug("Set refresh token in a secure httpOnly cookie");
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // Enable for HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(604800); // 7 days in seconds
        response.addCookie(refreshTokenCookie);

        log.debug("Return token, user ID, and eatery ID (refresh token is in cookie)");
        return new LoginResponse(jwt, user.getId(), eateryId);
    }

}
