package com.smartspeak.english_app.mail;

import com.smartspeak.english_app.mail.reset.ResetCode;
import com.smartspeak.english_app.mail.reset.ResetCodeRepository;
import com.smartspeak.english_app.mail.verif.VerificationCode;
import com.smartspeak.english_app.mail.verif.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    @Value("${smtp.from:}")
    private String fromAddress;

    @Value("${smtp.username:}")
    private String smtpUsername;

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;
    private final ResetCodeRepository resetCodeRepository;

    @Override
    public void sendVerificationCode(String email) {
        sendCode(email, "Ваш код для верификации: ", true);
    }

    @Override
    public void sendResetCode(String email) {
        // Поиск существующего кода сброса и его удаление
        Optional<ResetCode> existingCode = resetCodeRepository.findByEmail(email);
        existingCode.ifPresent(resetCodeRepository::delete);
        sendCode(email, "Ваш код для сброса пароля: ", false);
    }

    @Override
    public void sendCode(String email, String messagePrefix, boolean isVerification) {
        String code = generateCode();

        // Сохраняем код в репозитории в зависимости от типа
        if (isVerification) {
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setExpirationTime(LocalDateTime.now().plusMinutes(10));
            verificationCodeRepository.save(verificationCode);
        } else {
            ResetCode resetCode = new ResetCode();
            resetCode.setEmail(email);
            resetCode.setCode(code);
            resetCode.setExpirationTime(LocalDateTime.now().plusMinutes(10));
            resetCodeRepository.save(resetCode);
        }

        // Формируем HTML контент письма
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #212121;'>"
                + "<table role='presentation' style='width: 100%; background-color: #212121; padding: 20px;'>"
                + "<tr>"
                + "<td align='center'>"
                + "<table role='presentation' style='width: 600px; background-color: #333333; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.2);'>"
                + "<tr>"
                + "<td style='padding: 20px; text-align: center;'>"
                + "<h2 style='color: #E0E0E0;'>Ваш код для " + (isVerification ? "верификации" : "сброса пароля") + "</h2>"
                + "<p style='font-size: 16px; color: #E0E0E0;'>Здравствуйте!</p>"
                + "<p style='font-size: 18px; font-weight: bold; color: #E0E0E0;'>Ваш код: <span style='font-size: 24px; color: #FF9800;'>" + code + "</span></p>"
                + "<p style='font-size: 14px; color: #B0B0B0;'>Этот код действителен в течение 10 минут.</p>"
                + "<div style='text-align: center; margin-top: 20px;'>"
                + "<p style='font-size: 14px; color: #B0B0B0;'>С уважением,<br/>Ваша команда SmartSpeakSchool</p>"
                + "</div>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body></html>";

        // Создаем MimeMessage для отправки HTML письма
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");  // Инициализируем в блоке try

            // Заполняем и отправляем письмо
            helper.setTo(email);
            String sender = fromAddress == null || fromAddress.isBlank() ? smtpUsername : fromAddress;
            if (sender != null && !sender.isBlank()) {
                helper.setFrom(sender);
            }
            helper.setSubject(String.format("Код для %s", isVerification ? "верификации" : "сброса пароля"));
            helper.setText(htmlContent, true); // Указываем, что это HTML

            // Отправляем письмо
            mailSender.send(message);
            log.info("Verification or password-reset email sent");
        } catch (MessagingException e) {
            log.error("Ошибка при отправке письма: {}", e.getMessage());
            throw new RuntimeException("Ошибка при отправке письма: " + e.getMessage(), e);
        }
    }


    // Генерация случайного 6-значного кода
    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));  // Код будет всегда 6 знаков
    }

    @Override
    public void resendVerificationCode(String email) {
        // Поиск и удаление существующего кода верификации
        Optional<VerificationCode> existingCode = verificationCodeRepository.findByEmail(email);
        existingCode.ifPresent(verificationCodeRepository::delete);

        // Отправка нового кода
        sendVerificationCode(email);
    }
}
