package com.smartspeak.english_app.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    // Найти последнее устройство пользователя (где isLastDevice = true)
    Device findByUserAndIsLastDeviceTrue(User user);

    // Найти устройство пользователя по deviceId
    Device findByUserAndDeviceId(User user, String deviceId);
}
