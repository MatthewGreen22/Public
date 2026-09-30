package com.smartspeak.english_app.mail.reset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetCodeRepository extends JpaRepository<ResetCode, Long> {
    /**
     * Получает VerificationCode по указанному адресу электронной почты.
     *
     * @param email адрес электронной почты, для которого требуется получить VerificationCode
     * @return Optional, содержащий VerificationCode, связанный с указанным адресом электронной почты, или пустой, если не найден
     */
    Optional<ResetCode> findByEmail(String email);
}
