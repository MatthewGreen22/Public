package com.smartspeak.english_app.exceptions;

/**
 * Пользовательское исключение для представления ситуации, когда имя пользователя уже существует в системе.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * Создает новое исключение UsernameAlreadyExistsException с указанным детальным сообщением.
     *
     * @param message детальное сообщение (которое сохраняется для последующего извлечения с помощью метода getMessage())
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}