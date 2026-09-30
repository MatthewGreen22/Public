package com.smartspeak.english_app.jwt;


import com.smartspeak.english_app.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Представляет сущность токена, используемую для аутентификации и авторизации.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Component
public class Token {

    /**
     * Уникальный идентификатор токена.
     */
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    public UUID id;

    /**
     * Значение токена, которое является уникальным.
     */
    @Column(unique = true)
    public String token;

    /**
     * Пользователь, связанный с токеном, извлекаемый лениво.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}