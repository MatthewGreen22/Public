package com.smartspeak.english_app.handlers;

import com.smartspeak.english_app.exceptions.*;
import com.smartspeak.english_app.dto.responses.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для обработки InvalidPasswordException.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение InvalidPasswordException и возвращает ответ об ошибке с HTTP-статусом 400.
     *
     * @param ex InvalidPasswordException, которое требуется обработать
     * @return ResponseEntity, содержащий ErrorResponse с сообщением об ошибке и HTTP-статусом 400
     */
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<MessageResponse> handleInvalidPasswordException(InvalidCredentialException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение UsernameAlreadyExistsException и возвращает ответ об ошибке с HTTP-статусом 400.
     *
     * @param ex UsernameAlreadyExistsException, которое требуется обработать
     * @return ResponseEntity, содержащий ErrorResponse с сообщением об ошибке и HTTP-статусом 400
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение EmailAlreadyExistsException и возвращает ответ об ошибке с HTTP-статусом 400.
     *
     * @param ex EmailAlreadyExistsException, которое требуется обработать
     * @return ResponseEntity, содержащий ErrorResponse с сообщением об ошибке и HTTP-статусом 400
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<MessageResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение InvalidEmailException и возвращает ответ об ошибке с HTTP-статусом 400.
     *
     * @param ex InvalidEmailException, которое требуется обработать
     * @return ResponseEntity, содержащий ErrorResponse с сообщением об ошибке и HTTP-статусом 400
     */
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<MessageResponse> handleInvalidEmailException(InvalidEmailException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение InvalidUsernameException и возвращает ответ об ошибке с HTTP-статусом 400.
     *
     * @param ex InvalidUsernameException, которое требуется обработать
     * @return ResponseEntity, содержащий ErrorResponse с сообщением об ошибке и HTTP-статусом 400
     */
    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<MessageResponse> handleInvalidUsernameException(InvalidUsernameException ex) {
        MessageResponse messageResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }
}