package com.smartspeak.english_app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Value("${smtp.host:smtp.yandex.ru}")
    private String smtpHost;

    @Value("${smtp.port:465}")
    private int smtpPort;

    @Value("${smtp.username:}")
    private String smtpUsername;

    @Value("${smtp.password:}")
    private String smtpPassword;

    @Value("${smtp.from:}")
    private String smtpFrom;

    // порт хуй знает по какому принципу выбирается, но оно работает, так что лучше нихуя не трогать
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(smtpUsername);
        mailSender.setPassword(smtpPassword);

        Properties props = mailSender.getJavaMailProperties();
        if (smtpFrom != null && !smtpFrom.isBlank()) {
            props.put("mail.smtp.from", smtpFrom);
        }
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", Integer.toString(smtpPort));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", Integer.toString(smtpPort));
        props.put("mail.debug", "false");

        return mailSender;
    }
}
