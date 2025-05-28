package az.qrfood.backend.user.service;

import az.qrfood.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Пользовательская реализация UserDetailsService.
 * Используется Spring Security для загрузки информации о пользователе во время аутентификации.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Конструктор для внедрения UserRepository.
     * @param userRepository Репозиторий для доступа к данным пользователей.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает информацию о пользователе по его имени (логину).
     * Вызывается Spring Security во время процесса аутентификации.
     * @param username Имя пользователя для загрузки.
     * @return Объект UserDetails, представляющий пользователя.
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user not found: " + username));
    }
}