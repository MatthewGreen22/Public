// Пакет, в котором находится данный класс
package com.smartspeak.english_app.user;

// Импорт необходимых классов
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

// Класс UserDetailsServiceImpl, реализующий интерфейс UserDetailsService
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // UserRepository и PasswordEncoder инжектируются в конструктор
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    // Метод loadUserByUsername для загрузки информации о пользователе по имени пользователя
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Получение пользователя из репозитория по указанному имени пользователя
        Optional<User> user = userRepository.findByUsername(username);

        // Если пользователь не найден, выбрасывается исключение UsernameNotFoundException
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        // Возвращается объект UserDetails, созданный на основе полученной информации о пользователе
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
    }

    public boolean updateUserRole(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Обновляем роль пользователя
            user.setRole(Role.PREMIUM);
            userRepository.save(user);

            // Обновляем роль устройства, которое является последним активным
            Device lastDevice = deviceRepository.findByUserAndIsLastDeviceTrue(user);
            if (lastDevice != null) {
                lastDevice.setUserRole(Role.PREMIUM);  // Обновляем роль устройства
                deviceRepository.save(lastDevice);  // Сохраняем обновленное устройство
            }

            return true;
        }

        return false;
    }
}