package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.dto.MeasurementDTO;
import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.MeasurementRepository;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/measurements")
@CrossOrigin(origins = "*")
public class MeasurementController {

    private final MeasurementRepository measurementRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public MeasurementController(MeasurementRepository measurementRepository, 
                                 DeviceRepository deviceRepository, 
                                 UserRepository userRepository) {
        this.measurementRepository = measurementRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    // Endpoint pour récupérer toutes les mesures d’un device via son deviceId (ex: pour l'admin)
    @GetMapping("/by-device/{deviceId}")
    public ResponseEntity<List<Measurement>> getMeasurementsByDeviceId(@PathVariable String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }
        List<Measurement> measurements = measurementRepository.findByDevice(device);
        if (measurements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(measurements);
    }

    // Endpoint pour récupérer les mesures filtrées des devices assignés à l'utilisateur connecté
    @GetMapping("/mine")
    public ResponseEntity<List<MeasurementDTO>> getUserMeasurements(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Récupérer les devices assignés à l'utilisateur
        List<Device> userDevices = deviceRepository.findByUser(user);
        List<Measurement> measurements = new ArrayList<>();
        for (Device device : userDevices) {
            measurements.addAll(measurementRepository.findByDevice(device));
        }
        if (measurements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Mapper vers MeasurementDTO en tenant compte des permissions
        List<MeasurementDTO> dtos = measurements.stream().map(m -> {
            MeasurementDTO dto = new MeasurementDTO();
            dto.setTimestamp(m.getTimestamp());
            dto.setDeviceId(m.getDevice().getDeviceId());
            // Par exemple, si l'utilisateur a la permission de voir la température :
            if (user.getPermission() != null && user.getPermission().isCanViewTemperature()) {
                dto.setTemperature(m.getTemperature());
            }
            if (user.getPermission() != null && user.getPermission().isCanViewHumidity()) {
                dto.setHumidity(m.getHumidity());
            }
            if (user.getPermission() != null && user.getPermission().isCanViewLuminosite()) {
                dto.setLuminositeRaw(m.getLuminositeRaw());
            }
            if (user.getPermission() != null && user.getPermission().isCanViewHumiditeSol()) {
                dto.setHumiditeSolRaw(m.getHumiditeSolRaw());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/all")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<Measurement>> getAllMeasurements() {
    List<Measurement> allMeasurements = measurementRepository.findAll();
    if (allMeasurements.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(allMeasurements);
}

    // Endpoint pour supprimer une mesure par son ID
    @DeleteMapping("/delete/{measurementId}")
    public ResponseEntity<String> deleteMeasurement(@PathVariable Long measurementId) {
        if (!measurementRepository.existsById(measurementId)) {
            return ResponseEntity.status(404).body("❌ Mesure non trouvée.");
        }
        measurementRepository.deleteById(measurementId);
        return ResponseEntity.ok("✅ Mesure supprimée avec succès !");
    }

    // Endpoint pour ajouter manuellement une mesure à un device
    @PostMapping("/add")
    public ResponseEntity<String> addMeasurement(@RequestParam String deviceId,
                                                 @RequestParam float temperature,
                                                 @RequestParam float humidity,
                                                 @RequestParam int luminositeRaw,
                                                 @RequestParam int humiditeSolRaw) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            return ResponseEntity.badRequest().body("Device introuvable pour le deviceId: " + deviceId);
        }
        Measurement measurement = new Measurement(temperature, humidity, luminositeRaw, humiditeSolRaw, device);
        measurementRepository.save(measurement);
        return ResponseEntity.ok("Mesure ajoutée avec succès pour le device " + deviceId);
    }
}
