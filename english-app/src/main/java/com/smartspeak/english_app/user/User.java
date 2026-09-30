package com.smartspeak.english_app.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Класс, представляющий пользователя приложения и реализующий интерфейс UserDetails.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    // Поле идентификатора пользователя
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private UUID id;

    // Поле имени пользователя
    @Column(unique = true)
    private String username;

    // Поле пароля пользователя
    private String password;

    // Поле адреса электронной почты пользователя
    @Column(unique = true)
    private String email;

    // Поле роли пользователя
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Device> devices;

    /**
     * Метод для получения прав пользователя.
     * @return Коллекция прав пользователя в виде объектов типа GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Метод для получения пароля пользователя.
     * @return Пароль пользователя в виде строки.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Метод для получения имени пользователя.
     * @return Имя пользователя в виде строки.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Метод для проверки, не истек ли срок действия учетной записи пользователя.
     * @return true, если срок действия учетной записи пользователя не истек, иначе false.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Метод для проверки, не заблокирована ли учетная запись пользователя.
     * @return true, если учетная запись пользователя не заблокирована, иначе false.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Метод для проверки, не истек ли срок действия учетных данных пользователя.
     * @return true, если срок действия учетных данных пользователя не истек, иначе false.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Метод для проверки, включен ли пользователь.
     * @return true, если пользователь включен, иначе false.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}