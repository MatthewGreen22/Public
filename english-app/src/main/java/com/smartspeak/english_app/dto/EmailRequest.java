package com.smartspeak.english_app.dto;


import lombok.Data;

/**
 * Класс для передачи данных, представляющий запрос на отправку электронной почты.
 */
@Data
public class EmailRequest {

    /**
     * Адрес электронной почты.
     */
    private String email;

}