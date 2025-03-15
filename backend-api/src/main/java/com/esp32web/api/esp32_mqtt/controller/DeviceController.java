package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.MeasurementRepository;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository;

    public DeviceController(DeviceRepository deviceRepository, 
                            UserRepository userRepository, 
                            MeasurementRepository measurementRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.measurementRepository = measurementRepository;
    }

    // Récupérer tous les devices (pour l'admin)
    @GetMapping("/all")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(devices);
    }

    // Récupérer les mesures des devices assignés à l'utilisateur connecté
    // Vous pouvez accéder à cet endpoint avec l'authentification de l'utilisateur concerné
    @GetMapping("/mine-measurements")
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

    // Assigner un device à un utilisateur (accessible généralement aux admins)
    @PostMapping("/assign")
    public ResponseEntity<String> assignDeviceToUser(@RequestParam String deviceId,
                                                     @RequestParam String username) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        if (device == null) {
            return ResponseEntity.badRequest().body("Device introuvable pour le deviceId: " + deviceId);
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable: " + username);
        }
        device.setUser(user);
        deviceRepository.save(device);
        return ResponseEntity.ok("Device " + deviceId + " associé à l'utilisateur " + username);
    }

    // Supprimer un device (et ses mesures si la relation est en cascade)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id) {
        if (!deviceRepository.existsById(id)) {
            return ResponseEntity.status(404).body("❌ Device non trouvé.");
        }
        deviceRepository.deleteById(id);
        return ResponseEntity.ok("✅ Device supprimé avec succès !");
    }

    @PostMapping("/unassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unassignDeviceFromUser(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable : " + username);
        }
    
        List<Device> devices = deviceRepository.findByUser(user);
        for (Device device : devices) {
            device.setUser(null); // dissociation
            deviceRepository.save(device);
        }
    
        return ResponseEntity.ok("Tous les capteurs ont été dissociés de " + username);
    }
    

}
