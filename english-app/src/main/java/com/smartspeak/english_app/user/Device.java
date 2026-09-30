package com.smartspeak.english_app.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID устройства, с которого был выполнен вход
    private String deviceId;

    // Флаг, указывающий, является ли это устройство последним, с которого был выполнен вход
    private boolean isLastDevice;

    // Модель устройства
    private String deviceModel;

    // Связь с пользователем через ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    private String username;
}
