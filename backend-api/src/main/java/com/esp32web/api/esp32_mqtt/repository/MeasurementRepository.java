package com.esp32web.api.esp32_mqtt.repository;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    // Récupère toutes les mesures pour un device
    List<Measurement> findByDevice(Device device);
}
