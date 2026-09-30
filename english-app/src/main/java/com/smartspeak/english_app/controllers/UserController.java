package com.smartspeak.english_app.controllers;

import com.smartspeak.english_app.dto.UserRoleUpdateRequest;
import com.smartspeak.english_app.dto.responses.MessageResponse;
import com.smartspeak.english_app.user.UserDetailsServiceImpl;
import com.smartspeak.english_app.user.UserRepository;
import com.smartspeak.english_app.user.userfinder.UserInfoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public UserController(UserRepository userRepository, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userRepository = userRepository;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping("/getMe")
    public UserInfoDTO getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Получаем текущего аутентифицированного пользователя
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Возвращаем только нужные поля
        return new UserInfoDTO(user.getUsername(), user.getEmail(), user.getRole());
    }

    @PostMapping("/premium")
    public ResponseEntity<?> upgradeToPremium(@RequestBody UserRoleUpdateRequest request) {
        boolean upgraded = userDetailsServiceImpl.updateUserRole(request.getUsername());

        if (upgraded) {
            return ResponseEntity.ok(new MessageResponse("Роль пользователя успешно изменена на PREMIUM."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Не удалось изменить роль пользователя."));
        }
    }

    @GetMapping("/username")
    public ResponseEntity<?> checkIfUserExists(@RequestParam String username) {
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserInfoDTO(user.get().getUsername(), user.get().getEmail(), user.get().getRole()));
        } else {
            // Используем MessageResponse для отправки ошибки
            return ResponseEntity.status(404).body(new MessageResponse("Пользователь с таким юзернеймом не найден"));
        }
    }
}