package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.MeasurementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@CrossOrigin(origins = "*")
public class MeasurementController {

    private final MeasurementRepository measurementRepository;
    private final DeviceRepository deviceRepository;

    public MeasurementController(MeasurementRepository measurementRepository, DeviceRepository deviceRepository) {
        this.measurementRepository = measurementRepository;
        this.deviceRepository = deviceRepository;
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

    // Supprimer une mesure précise par son ID
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
