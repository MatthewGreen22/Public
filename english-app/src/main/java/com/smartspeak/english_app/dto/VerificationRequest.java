package com.smartspeak.english_app.dto;

import lombok.Data;

/**
 * Класс, представляющий запрос на верификацию, содержащий адрес электронной почты и код.
 */
@Data
public class VerificationRequest {
    /**
     * Адрес электронной почты для запроса на верификацию.
     */
    private String email;

    /**
     * Код верификации, связанный с запросом.
     */
    private String code;

    private String deviceId;

    private String DeviceModel;

}
