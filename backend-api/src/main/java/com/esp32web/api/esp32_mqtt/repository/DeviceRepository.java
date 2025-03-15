package com.esp32web.api.esp32_mqtt.repository;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    // Récupère un device via son deviceId unique
    Device findByDeviceId(String deviceId);

    // Récupère tous les devices appartenant à un user donné
    List<Device> findByUser(User user);
}
