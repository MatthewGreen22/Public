package com.smartspeak.english_app.config;

import com.smartspeak.english_app.jwt.JwtAuthenticationFilter;
import com.smartspeak.english_app.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Настраивает цепочку фильтров безопасности для приложения.
     * <p>
     * Этот метод устанавливает конфигурацию безопасности для приложения, включая
     * отключение защиты CSRF, определение правил авторизации, настройку политики управления сессиями,
     * добавление фильтра JWT-авторизации и обработку исключений.
     *
     * @param http объект {@link HttpSecurity}, который нужно изменить
     * @return объект {@link SecurityFilterChain}, представляющий конфигурацию безопасности
     * @throws Exception если при настройке цепочки фильтров безопасности происходит ошибка
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req.requestMatchers(
                                        "/password-reset/**",
                                        "/send-password-reset/**",
                                        "/login/**",
                                        "/resend/**",
                                        "/verify/**",
                                        "/register/**"
                                )

                                .permitAll()

                                .requestMatchers(
                                        "/getExam/**",
                                        "/getTraining/**"
                                )
                                .hasRole("PREMIUM")
                                .requestMatchers(
                                        "/premium",
                                        "/username",
                                        "/create-listening/**",
                                        "/create-reading/**",
                                        "/create-writing/**",
                                        "/create-speaking/**",
                                        "/create-grammar/**",
                                        "/createExam/**",
                                        "/createTraining/**"
                                )
                                .hasRole("ADMIN")
                                .requestMatchers(
                                        "/html/**",
                                        "/listening/**",
                                        "/reading/**",
                                        "/writing/**",
                                        "/speaking/**",
                                        "/grammar/**",
                                        "/getMe/**")


                                .hasAnyRole("USER","ADMIN", "PREMIUM")
                                .anyRequest()
                                .authenticated()
                ).userDetailsService(userDetailsServiceImpl)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    // Возвращаем 403 Forbidden для отказа в доступе
                                    response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
                                })
                                .authenticationEntryPoint((request, response, authException) -> {
                                    // Если аутентификация не пройдена, возвращаем 401 Unauthorized
                                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                                })
                )
                .build();
    }

    /**
     * Создает и возвращает экземпляр объекта, реализующего интерфейс PasswordEncoder,
     * который использует алгоритм хеширования BCrypt для шифрования паролей.
     *
     * @return экземпляр объекта, реализующего интерфейс PasswordEncoder, с использованием алгоритма BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Создает и возвращает экземпляр объекта, реализующего интерфейс AuthenticationManager,
     * который использует предоставленный объект PasswordEncoder для шифрования паролей
     * и объект UserDetailsService для получения информации о пользователях.
     *
     * @param passwordEncoder    объект, реализующий интерфейс PasswordEncoder, используемый для шифрования паролей
     * @param userDetailsService объект, реализующий интерфейс UserDetailsService, используемый для получения информации о пользователях
     * @return экземпляр объекта, реализующего интерфейс AuthenticationManager, сконфигурированный с предоставленным PasswordEncoder и UserDetailsService
     */
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }
}


//                                        "/create-listening",
//                                        "/listening",
//                                        "/update-listening/{id}",
//                                        "/create-reading",
//                                        "/reading",
//                                        "/update-reading {id}",
//                                        "create-writing",
//                                        "/writing",
//                                        "/update-writing/{id}",
//                                        "/create-speaking",
//                                        "/speaking",
//                                        "/update-speaking{id}",
//                                        "/create-grammar",
//                                        "/grammar",
//                                        "/update-grammar/{id}"
