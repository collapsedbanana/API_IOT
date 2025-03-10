package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Import nécessaire
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceController(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    // Récupérer tous les devices (ex: pour admin)
    @GetMapping("/all")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(devices);
    }

    // Endpoint pour récupérer les devices assignés à l'utilisateur connecté
    @GetMapping("/mine")
    public ResponseEntity<List<Device>> getUserDevices(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        List<Device> devices = deviceRepository.findByUser(user);
        if (devices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(devices);
    }

    // Assigner un device à un utilisateur (généralement accessible aux admins)
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

    // Supprimer un device (et potentiellement ses mesures via cascade)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id) {
        if (!deviceRepository.existsById(id)) {
            return ResponseEntity.status(404).body("❌ Device non trouvé.");
        }
        deviceRepository.deleteById(id);
        return ResponseEntity.ok("✅ Device supprimé avec succès !");
    }
}
