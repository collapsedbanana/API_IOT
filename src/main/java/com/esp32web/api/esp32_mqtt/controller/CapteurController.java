package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Capteur;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/capteurs")
public class CapteurController {

    private final CapteurRepository capteurRepository;
    private final UserRepository userRepository;

    public CapteurController(CapteurRepository capteurRepository, UserRepository userRepository) {
        this.capteurRepository = capteurRepository;
        this.userRepository = userRepository;
    }

    // Endpoint pour récupérer tous les capteurs (ADMIN uniquement)
    @GetMapping("/all")
    public ResponseEntity<List<Capteur>> getAllCapteurs() {
        List<Capteur> capteurs = capteurRepository.findAll();
        if (capteurs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(capteurs);
    }

    // Endpoint pour récupérer un capteur par son ID (ADMIN uniquement)
    @GetMapping("/{id}")
    public ResponseEntity<Capteur> getCapteurById(@PathVariable Long id) {
        Optional<Capteur> capteur = capteurRepository.findById(id);
        return capteur.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour supprimer un capteur par son ID (ADMIN uniquement)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCapteur(@PathVariable Long id) {
        if (capteurRepository.existsById(id)) {
            capteurRepository.deleteById(id);
            return ResponseEntity.ok("✅ Capteur supprimé avec succès !");
        } else {
            return ResponseEntity.status(404).body("❌ Capteur non trouvé.");
        }
    }

    // Endpoint pour récupérer les capteurs de l'utilisateur connecté (accessible à tous les authentifiés)
    @GetMapping("/mine")
    public ResponseEntity<List<Capteur>> getUserCapteurs(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        List<Capteur> capteurs = capteurRepository.findByUser(user);
        if (capteurs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(capteurs);
    }
    
    // Endpoint pour assigner un capteur à un utilisateur (ADMIN uniquement)
    @PostMapping("/assign")
    public ResponseEntity<String> assignCapteurToUser(@RequestParam Long capteurId, @RequestParam String username) {
        Optional<Capteur> optCapteur = capteurRepository.findById(capteurId);
        if (!optCapteur.isPresent()) {
            return ResponseEntity.badRequest().body("Capteur introuvable");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable");
        }
        Capteur capteur = optCapteur.get();
        capteur.setUser(user);
        capteurRepository.save(capteur);
        return ResponseEntity.ok("Capteur " + capteurId + " associé à l'utilisateur " + username);
    }
}
