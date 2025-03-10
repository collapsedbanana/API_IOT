package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.MeasurementRepository;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Import nécessaire
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@CrossOrigin(origins = "*")
public class MeasurementController {

    private final MeasurementRepository measurementRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public MeasurementController(MeasurementRepository measurementRepository, DeviceRepository deviceRepository, UserRepository userRepository) {
        this.measurementRepository = measurementRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    // Récupérer toutes les mesures d’un device via son deviceId
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

    // Endpoint pour récupérer les mesures des devices assignés à l'utilisateur connecté
    @GetMapping("/mine")
    public ResponseEntity<List<Measurement>> getUserMeasurements(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        List<Device> userDevices = deviceRepository.findByUser(user);
        List<Measurement> userMeasurements = new ArrayList<>();
        for (Device device : userDevices) {
            userMeasurements.addAll(measurementRepository.findByDevice(device));
        }
        if (userMeasurements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userMeasurements);
    }

    // Supprimer une mesure par son ID
    @DeleteMapping("/delete/{measurementId}")
    public ResponseEntity<String> deleteMeasurement(@PathVariable Long measurementId) {
        if (!measurementRepository.existsById(measurementId)) {
            return ResponseEntity.status(404).body("❌ Mesure non trouvée.");
        }
        measurementRepository.deleteById(measurementId);
        return ResponseEntity.ok("✅ Mesure supprimée avec succès !");
    }

    // (Optionnel) Ajouter manuellement une mesure à un device
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
