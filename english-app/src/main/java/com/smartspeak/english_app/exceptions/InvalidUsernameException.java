package com.smartspeak.english_app.exceptions;


/**
 * Класс пользовательского исключения для неверного имени пользователя.
 */
public class InvalidUsernameException extends RuntimeException {

    /**
     * Создает новое исключение InvalidUsernameException с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке (которое сохраняется для последующего извлечения с помощью метода getMessage())
     */
    public InvalidUsernameException(String message) {
        super(message);
    }
}

