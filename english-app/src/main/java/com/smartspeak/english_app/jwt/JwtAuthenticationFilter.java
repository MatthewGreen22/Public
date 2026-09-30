package com.smartspeak.english_app.jwt;

import com.smartspeak.english_app.user.userfinder.UserFinder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final List<UserFinder> userFinders; // Внедряем список всех реализаций UserFinder

    // Путь для регистрации и других запросов, которые должны быть исключены из проверки JWT
    private static final String[] EXCLUDED_PATHS = {
            "/login",
            "/resend",
            "/verify",
            "/register"
    };

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Получение URI запроса
        String requestURI = request.getRequestURI();

        // Проверка, является ли запрос запросом, который нужно игнорировать
        if (shouldSkipJwtCheck(requestURI)) {
            // Пропустить проверку JWT и передать запрос дальше
            filterChain.doFilter(request, response);
            return;
        }

        // Извлечение заголовка авторизации
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String usernameOrEmail;

        // Если заголовок отсутствует или не начинается с "Bearer ", передаем запрос дальше
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлечение JWT и имени пользователя или email
        jwt = authHeader.substring(7);
        usernameOrEmail = jwtService.extractUsername(jwt);

        // Проверка на существование пользователя в контексте безопасности
        if (usernameOrEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Ищем пользователя по всем возможным реализациям UserFinder
            Optional<UserDetails> user = findUserByAllFinders(jwt);

            if (user.isPresent() && jwtService.isTokenValid(jwt, user.get())) {
                List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(jwt)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.get(), null, authorities);

                // Установка деталей аутентификации запроса
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Установка токена аутентификации в контексте безопасности
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Передача запроса дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }

    // Метод для поиска пользователя через все реализации UserFinder
    private Optional<UserDetails> findUserByAllFinders(String token) {
        for (UserFinder finder : userFinders) {
            UserDetails user = finder.findByToken(token);
            if (user != null) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // Метод для проверки, нужно ли пропускать проверку JWT
    private boolean shouldSkipJwtCheck(String requestURI) {
        for (String path : EXCLUDED_PATHS) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
