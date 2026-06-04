package org.example.ecommerceapi.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private AppUser(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static AppUser createUser(String email, String encodedPassword) {
        return new AppUser(email, encodedPassword, Role.USER);
    }

}
