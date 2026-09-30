package com.smartspeak.english_app.controllers;


import com.smartspeak.english_app.dto.*;
import com.smartspeak.english_app.dto.responses.AuthenticationResponse;
import com.smartspeak.english_app.auth.AuthenticationService;
import com.smartspeak.english_app.dto.responses.MessageResponse;
import com.smartspeak.english_app.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Контроллер для обработки операций аутентификации пользователя, таких как регистрация и вход в систему.
 */
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    /**
     * Конечная точка для регистрации пользователя.
     *
     * @param request Запрос на регистрацию, содержащий данные пользователя.
     * @return ResponseEntity, содержащий ответ на аутентификацию при успешной регистрации.
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Конечная точка для входа пользователя.
     *
     * @param request Запрос на аутентификацию, содержащий учетные данные пользователя.
     * @return ResponseEntity, содержащий ответ на аутентификацию при успешном входе.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));

    }

    /**
     * Конечная точка для проверки подтверждения пользователя.
     *
     * @param request Запрос на подтверждение, содержащий адрес электронной почты пользователя и код подтверждения.
     * @return ResponseEntity, содержащий сообщение о результате проверки подтверждения пользователя.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerificationRequest request) {
        // Добавлен deviceModel в аргументы
        boolean verified = authenticationService.verifyUser(request.getEmail(), request.getCode(), request.getDeviceId(), request.getDeviceModel());
        if (verified) {
            return ResponseEntity.ok(new MessageResponse("Пользователь успешно подтвержден."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Неверный код подтверждения."));
        }
    }


    /**
     * Конечная точка для повторной отправки кода подтверждения.
     *
     * @param request Запрос на повторную отправку кода подтверждения, содержащий адрес электронной почты пользователя.
     * @return ResponseEntity, содержащий сообщение об успешной повторной отправке кода подтверждения.
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody VerificationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            authenticationService.resendVerificationCode(request.getEmail());
            return ResponseEntity.ok(new MessageResponse("Код подтверждения отправлен повторно."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Адрес электронной почты не существует."));
        }
    }

    /**
     * Конечная точка для обновления токена аутентификации.
     *
     * @param request  HTTP-запрос, содержащий информацию о текущем токене аутентификации.
     * @param response HTTP-ответ, в который будет записан новый токен аутентификации.
     * @throws IOException если возникают проблемы при записи нового токена в HTTP-ответ.
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        authenticationService.refreshToken(request, response);
    }

    /**
     * Конечная точка для сброса пароля пользователя.
     *
     * @param request Запрос на сброс пароля, содержащий данные пользователя и новый пароль.
     * @return ResponseEntity, содержащий сообщение о результате сброса пароля.
     */
    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {

        boolean success = authenticationService.resetPassword(request);
        if (success) {
            return ResponseEntity.ok(new MessageResponse("Пароль успешно изменен."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Неверный код подтверждения."));
        }
    }

    /**
     * Конечная точка для отправки кода сброса пароля на адрес электронной почты пользователя.
     *
     * @param request Запрос на отправку кода сброса пароля, содержащий адрес электронной почты пользователя.
     * @return ResponseEntity, содержащий сообщение о результате отправки кода сброса пароля.
     */
    @PostMapping("/send-password-reset")
    public ResponseEntity<?> sendResetCode(@RequestBody EmailRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            authenticationService.sendResetCode(request.getEmail());
            return ResponseEntity.ok(new MessageResponse("Код подтверждения отправлен на почту."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Адрес электронной почты не существует."));
        }
    }
}