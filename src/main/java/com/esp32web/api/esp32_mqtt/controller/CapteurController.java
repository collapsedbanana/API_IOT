package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Capteur;
import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*") // Autorise les requêtes depuis n'importe quel domaine (CORS)
@RestController
@RequestMapping("/api/capteurs")
public class CapteurController {

    private final CapteurRepository capteurRepository;

    public CapteurController(CapteurRepository capteurRepository) {
        this.capteurRepository = capteurRepository;
    }

    // ✅ Endpoint pour récupérer tous les capteurs
    @GetMapping("/all")
    public ResponseEntity<List<Capteur>> getAllCapteurs() {
        List<Capteur> capteurs = capteurRepository.findAll();
        if (capteurs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(capteurs);
    }

    // ✅ Endpoint pour récupérer un capteur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Capteur> getCapteurById(@PathVariable Long id) {
        Optional<Capteur> capteur = capteurRepository.findById(id);
        return capteur.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Endpoint pour supprimer un capteur par son ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCapteur(@PathVariable Long id) {
        if (capteurRepository.existsById(id)) {
            capteurRepository.deleteById(id);
            return ResponseEntity.ok("✅ Capteur supprimé avec succès !");
        } else {
            return ResponseEntity.status(404).body("❌ Capteur non trouvé.");
        }
    }
}
