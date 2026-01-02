package az.qrfood.backend.auth.service;

import az.qrfood.backend.auth.entity.AuthToken;
import az.qrfood.backend.auth.repository.AuthTokenRepository;
import az.qrfood.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static az.qrfood.backend.auth.util.Util.sha256;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    @Transactional
    public String createMagicLinkToken(User user) {
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken();
        authToken.setTokenHash(sha256(token));
        authToken.setUser(user);
        authToken.setTokenType(AuthToken.TokenType.MAGIC_LINK);
        authToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.MINUTES));
        authTokenRepository.save(authToken);
        return token;
    }
}