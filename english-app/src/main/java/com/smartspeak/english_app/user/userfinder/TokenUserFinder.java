package com.smartspeak.english_app.user.userfinder;

import com.smartspeak.english_app.jwt.JwtService;
import com.smartspeak.english_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenUserFinder implements UserFinder {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public UserDetails findByToken(String token) {
        // Извлечение имени пользователя из токена
        String username = jwtService.extractUsername(token);

        // Поиск пользователя по имени в базе данных
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден по токену"));
    }
}
