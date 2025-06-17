package az.qrfood.backend.useraccess.entity;

import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_access")
@Setter
@Getter
public class UserAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Eatery eatery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
