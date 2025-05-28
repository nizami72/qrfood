package az.qrfood.backend.user.repository;

import az.qrfood.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для доступа к данным пользователей.
 * Предоставляет стандартные CRUD-операции и метод для поиска пользователя по имени.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по его имени (логину).
     * @param username Имя пользователя для поиска.
     * @return Optional, содержащий пользователя, если найден, иначе пустой Optional.
     */
    Optional<User> findByUsername(String username);
}
