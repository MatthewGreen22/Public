package com.smartspeak.english_app.config.validators;


import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    /**
     * Проверяет, соответствует ли предоставленный пароль заданным критериям надежности.
     *
     * @param password пароль для проверки
     * @return true, если пароль соответствует критериям надежности, в противном случае - false
     */
    public boolean isPasswordValid(String password) {
        // Проверка наличия спец символа, заглавной буквы, цифры и длины пароля не менее 10 символов
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasRequiredLength = password.length() >= 10;

        return hasSpecialChar && hasUpperCase && hasDigit && hasRequiredLength;
    }
}