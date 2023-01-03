package com.nono.deluxe.domain.user;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean active;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean deleted;

    @Builder
    public User(String name, String email, String password, Role role, boolean active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.deleted = false;
    }

    public static User createParticipant(String name) {
        return User.builder()
            .name(name)
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .role(Role.ROLE_PARTICIPANT)
            .active(true)
            .build();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    public void delete() {
        this.deleted = true;
        this.active = false;
        this.name = UUID.randomUUID().toString().substring(0, 18);
    }

    public void updateByAdmin(String name, boolean active) {
        if (role.equals(Role.ROLE_PARTICIPANT)) {
            this.name = name;
        }
        this.active = active;
    }

    public void update(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
