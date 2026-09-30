package com.smartspeak.english_app.mail;


import org.springframework.stereotype.Repository;

/**
 * Этот интерфейс представляет собой сервис электронной почты для отправки и повторной отправки верификационных кодов.
 */
@Repository
public interface MailService {
    /**
     * Отправляет верификационный код на указанный адрес электронной почты.
     *
     * @param email адрес электронной почты, на который будет отправлен верификационный код
     */
    void sendVerificationCode(String email);

    void sendCode(String email, String messagePrefix, boolean isVerification);

    /**
     * Повторно отправляет верификационный код на указанный адрес электронной почты.
     *
     * @param email адрес электронной почты, на который будет отправлен верификационный код повторно
     */
    void resendVerificationCode(String email);


    void sendResetCode(String email);


}
