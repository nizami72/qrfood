package az.qrfood.backend.user;

import az.qrfood.backend.user.profile.UserProfile;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сущность пользователя, представляющая запись в базе данных.
 * Реализует UserDetails для интеграции со Spring Security.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Список ролей пользователя, например, "ROLE_USER", "ROLE_ADMIN"
    // Используем ElementCollection для хранения ролей в отдельной таблице
    @ElementCollection(fetch = FetchType.EAGER) // Получаем роли сразу при загрузке пользователя
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    /**
     * The user profile associated with this user.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    /**
     * Возвращает коллекции прав доступа (ролей), предоставленных пользователю.
     * @return Коллекция GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new) // Преобразуем строковые роли в SimpleGrantedAuthority
                .collect(Collectors.toList());
    }

    /**
     * Указывает, не истек ли срок действия учетной записи пользователя.
     * @return true, если учетная запись действительна (не истек срок действия), false в противном случае.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // В реальном приложении может быть логика истечения срока действия аккаунта
    }

    /**
     * Указывает, не заблокирована ли учетная запись пользователя.
     * @return true, если учетная запись не заблокирована, false в противном случае.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // В реальном приложении может быть логика блокировки аккаунта
    }

    /**
     * Указывает, не истек ли срок действия учетных данных (пароля) пользователя.
     * @return true, если учетные данные действительны (не истек срок действия), false в противном случае.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // В реальном приложении может быть логика истечения срока действия учетных данных
    }

    /**
     * Указывает, включен ли пользователь или отключен.
     * @return true, если пользователь включен, false в противном случае.
     */
    @Override
    public boolean isEnabled() {
        return true; // В реальном приложении может быть логика включения/отключения аккаунта
    }
}
