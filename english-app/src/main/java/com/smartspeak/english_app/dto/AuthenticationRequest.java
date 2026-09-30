package com.smartspeak.english_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет запрос на аутентификацию, содержащий имя пользователя и пароль.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /**
     * Имя пользователя для аутентификации.
     */
    private String username;

    /**
     * Пароль для аутентификации.
     */
    private String password;

    private String deviceId;

    public String deviceModel;
}

