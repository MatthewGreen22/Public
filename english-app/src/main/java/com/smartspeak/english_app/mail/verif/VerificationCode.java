package com.smartspeak.english_app.mail.verif;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
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
public class VerificationCode {
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
