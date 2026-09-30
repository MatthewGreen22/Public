package com.smartspeak.english_app.mail.reset;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Представляет сущность верификационного кода для подтверждения адреса электронной почты.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetCode {
    /**
     * Уникальный идентификатор верификационного кода.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Адрес электронной почты, связанный с верификационным кодом.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Значение верификационного кода.
     */
    @Column(nullable = false)
    private String code;


    /**
     * Время истечения срока действия верификационного кода.
     */
    @Column(nullable = false)
    private LocalDateTime expirationTime;

}
