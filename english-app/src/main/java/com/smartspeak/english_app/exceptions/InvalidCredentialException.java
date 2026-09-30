package com.smartspeak.english_app.exceptions;

/**
 * Класс пользовательского исключения для неверного пароля.
 */
public class InvalidCredentialException extends RuntimeException {

    /**
     * Создает новое исключение InvalidPasswordException с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке (которое сохраняется для последующего извлечения с помощью метода getMessage())
     */
    public InvalidCredentialException(String message) {
        super(message);
    }
}