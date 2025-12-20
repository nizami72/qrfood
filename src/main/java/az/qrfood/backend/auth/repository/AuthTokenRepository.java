package az.qrfood.backend.auth.repository;

import az.qrfood.backend.auth.entity.AuthToken;
import az.qrfood.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);

}
