package com.smartspeak.english_app.dto;


import lombok.Data;

/**
 * Класс для передачи данных, представляющий запрос на сброс пароля.
 */
@Data
public class PasswordResetRequest {

    /**
     * Адрес электронной почты, связанный с запросом на сброс пароля.
     */
    private String email;

    /**
     * Код подтверждения для запроса на сброс пароля.
     */
    private String code;

    /**
     * Новый пароль, который будет установлен после сброса пароля.
     */
    private String newPassword;
}
