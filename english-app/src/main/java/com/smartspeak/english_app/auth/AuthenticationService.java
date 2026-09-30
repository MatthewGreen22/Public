package com.smartspeak.english_app.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartspeak.english_app.dto.AuthenticationRequest;
import com.smartspeak.english_app.dto.RegisterRequest;
import com.smartspeak.english_app.dto.PasswordResetRequest;
import com.smartspeak.english_app.dto.responses.MessageResponse;
import com.smartspeak.english_app.exceptions.*;
import com.smartspeak.english_app.jwt.JwtService;
import com.smartspeak.english_app.jwt.Token;
import com.smartspeak.english_app.jwt.TokenRepository;
import com.smartspeak.english_app.mail.MailService;
import com.smartspeak.english_app.dto.responses.AuthenticationResponse;
import com.smartspeak.english_app.mail.reset.ResetCode;
import com.smartspeak.english_app.mail.reset.ResetCodeRepository;
import com.smartspeak.english_app.user.*;
import com.smartspeak.english_app.config.validators.PasswordValidator;
import com.smartspeak.english_app.mail.verif.VerificationCode;
import com.smartspeak.english_app.mail.verif.VerificationCodeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Сервис для обработки операций аутентификации пользователя, таких как регистрация и вход в систему.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordValidator passwordValidator;
    private final TokenRepository tokenRepository;
    private final ResetCodeRepository resetCodeRepository;
    private final MailService mailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final DeviceRepository deviceRepository;


    /**
     * Регистрирует нового пользователя с предоставленным запросом на регистрацию.
     * Проверяет корректность и уникальность электронной почты, пароля и имени пользователя.
     * Если проверка прошла успешно, сохраняет пользователя в репозитории, отправляет код подтверждения на электронную почту пользователя,
     * генерирует JWT-токен для пользователя и возвращает объект AuthenticationResponse, содержащий токен доступа.
     *
     * @param request запрос на регистрацию, содержащий электронную почту, имя пользователя и пароль пользователя
     * @return объект AuthenticationResponse, содержащий JWT-токен доступа
     * @throws EmailAlreadyExistsException    если предоставленная электронная почта уже зарегистрирована
     * @throws InvalidEmailException          если предоставленная электронная почта недопустима
     * @throws InvalidCredentialException       если предоставленный пароль не соответствует требуемым критериям
     * @throws InvalidUsernameException       если предоставленное имя пользователя недопустимо
     * @throws UsernameAlreadyExistsException если предоставленное имя пользователя уже существует
     */
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Такая почта уже зарегистрирована");
        }

        if (!request.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidEmailException("Невалидный адрес электронной почты");
        }

        if (!passwordValidator.isPasswordValid(request.getPassword())) {
            throw new InvalidCredentialException("Невалидный пароль. Пароль должен содержать хотя бы одну заглавную букву, хотя бы один спец символ, хотя бы одну цифру и минимум 10 символов");
        }

        if (request.getUsername().length() < 4) {
            throw new InvalidUsernameException("Невалидное имя пользователя. Минимальная длина имени пользователя - 4 символов");
        }

        if (request.getUsername().length() > 10) {
            throw new InvalidUsernameException("Невалидное имя пользователя. максимальная длина имени пользователя - 10 символов");
        }

        if (!request.getUsername().matches("^[a-zA-Z0-9_-]{4,}$")) {
            throw new InvalidUsernameException("Невалидное имя пользователя. Разрешены только буквы латинского алфавита, цифры, дефисы и подчеркивания");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Имя пользователя уже существует");
        }


        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.UNVERIFIED) // или назначьте другую роль
                .build();
        userRepository.save(user);

        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .isLastDevice(true)
                .user(user)
                .username(user.getUsername())
                .userRole(user.getRole())
                .build();
        deviceRepository.save(device);

        mailService.sendVerificationCode(user.getEmail());


        return new MessageResponse("Код отправлен на почту");
    }

    /**
     * Аутентифицирует пользователя на основе предоставленного имени пользователя и пароля.
     * Если аутентификация прошла успешно, генерирует JWT-токен для пользователя.
     *
     * @param request запрос аутентификации, содержащий имя пользователя и пароль
     * @return объект AuthenticationResponse, содержащий JWT-токен доступа
     */
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {

        // Найдем пользователя по имени
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialException("Пользователь не найден"));

        // Проверка пароля
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Неверный логин или пароль");
        }

        // Проверка подтверждения пользователя
        if (user.getRole() == Role.UNVERIFIED) {
            throw new InvalidCredentialException("Пользователь не подтвержден");
        }

        // Аутентификация пользователя
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Проверка Device ID
        Device lastDevice = deviceRepository.findByUserAndIsLastDeviceTrue(user);
        if (lastDevice == null || !lastDevice.getDeviceId().equals(request.getDeviceId())) {
            // Если устройство новое, обновляем роль пользователя
            user.setRole(Role.USER);
            userRepository.save(user);

            // Обновляем устройство
            updateDeviceForUser(user, request.getDeviceId(), request.getDeviceModel());
        }

        // Генерация токенов
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Управление токенами
        revokeAllUserTokens(user);
        saveUserToken(user, refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        tokenRepository.deleteAll(validUserTokens);
    }


    /**
     * Повторно отправляет код подтверждения на указанный адрес электронной почты.
     *
     * @param email адрес электронной почты пользователя
     */

    public void resendVerificationCode(String email) {
        mailService.resendVerificationCode(email);
    }

    public void sendResetCode(String email) {
        mailService.sendResetCode(email);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        // Проверка наличия заголовка авторизации и его формата
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Если заголовок авторизации отсутствует или не начинается с "Bearer ",
            // передаем запрос дальше по цепочке фильтров и выходим из метода
            return;
        }

        // Извлечение JWT из заголовка авторизации, удаляя префикс "Bearer "
        refreshToken = authHeader.substring(7);

        // Парсинг JWT и извлечение имени пользователя (subject) из токена
        username = jwtService.extractUsername(refreshToken);

        // Проверка наличия имени пользователя в контексте безопасности
        if (username != null) {
            // Загрузка деталей пользователя для извлеченного имени пользователя
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();

            // Проверка действительности JWT токена с использованием деталей пользователя
            if (jwtService.isTokenValid(refreshToken, user)) {

                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

            }
        }
    }

    public boolean resetPassword(PasswordResetRequest request) {
        ResetCode resetCode = resetCodeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Код сброса не найден"));

        if (resetCode.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialException("Код сброса истек");
        }

        if (!passwordValidator.isPasswordValid(request.getNewPassword())) {
            throw new InvalidCredentialException("Невалидный новый пароль. Пароль должен содержать хотя бы одну заглавную букву, хотя бы один спец символ, хотя бы одну цифру и минимум 10 символов");
        }

        if (resetCode.getCode().equals(request.getCode())) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialException("Пользователь не найден"));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            resetCodeRepository.delete(resetCode);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Проверяет подтверждение пользователя с использованием предоставленного адреса электронной почты и кода подтверждения.
     * Если код подтвержд��ния действителен и не истек, роль пользователя обновляется на 'USER',
     * и код подтверждения удаляется из репозитория.
     *
     * @param email адрес электронной почты пользователя
     * @param code  код подтверждения для проверки
     * @return true, если пользователь успешно подтвержден, иначе false
     */
    @Transactional
    public boolean verifyUser(String email, String code, String deviceId, String deviceModel) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Код подтверждения не найден"));

        if (verificationCode.getCode().equals(code) && verificationCode.getExpirationTime().isAfter(LocalDateTime.now())) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            // Обновляем роль пользователя
            user.setRole(Role.USER);
            userRepository.save(user);

            // Обновляем устройство пользователя
            updateDeviceForUser(user, deviceId, deviceModel);  // Теперь передаем deviceModel

            // Удаляем код подтверждения
            verificationCodeRepository.delete(verificationCode);

            return true;
        } else {
            return false;
        }
    }


    @Transactional
    public void updateDeviceForUser(User user, String newDeviceId, String deviceModel) {
        // Найдем предыдущее устройство, которое было помечено как "последнее"
        Device lastDevice = deviceRepository.findByUserAndIsLastDeviceTrue(user);

        // Если найдено предыдущее устройство и оно отличается от текущего
        if (lastDevice != null && !lastDevice.getDeviceId().equals(newDeviceId)) {
            // Обновляем статус старого устройства
            lastDevice.setLastDevice(false);
            deviceRepository.save(lastDevice);
        }

        // Найдем текущее устройство пользователя
        Device existingDevice = deviceRepository.findByUserAndDeviceId(user, newDeviceId);

        if (existingDevice != null) {
            // Если устройство уже существует, обновляем его статус как последнее
            existingDevice.setLastDevice(true);
            existingDevice.setUserRole(user.getRole()); // Обновляем роль пользователя
            existingDevice.setDeviceModel(deviceModel); // Обновляем модель устройства
            deviceRepository.save(existingDevice);
        } else {
            // Если устройство новое, создаем его и записываем роль пользователя
            Device newDevice = Device.builder()
                    .deviceId(newDeviceId)
                    .isLastDevice(true)
                    .user(user)
                    .username(user.getUsername())
                    .userRole(user.getRole()) // Передаем роль пользователя
                    .deviceModel(deviceModel) // Передаем модель устройства
                    .build();
            deviceRepository.save(newDevice);
        }
    }
}
