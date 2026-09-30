package com.smartspeak.english_app.dto.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * Представляет ответ содержащий message.
 */
@Getter
@Setter
public class MessageResponse {

    private String message;

    /**
     * Создает объект MessageResponse с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public MessageResponse(String message) {
        this.message = message;
    }
}