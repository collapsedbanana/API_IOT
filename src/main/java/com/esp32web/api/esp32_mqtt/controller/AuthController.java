package com.esp32web.api.esp32_mqtt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Tentative d'enregistrement pour l'utilisateur: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Utilisateur {} déjà existant", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilisateur déjà existant");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        logger.info("Utilisateur {} enregistré avec succès", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur enregistré avec succès");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        logger.info("Tentative de connexion pour l'utilisateur: {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            logger.warn("Utilisateur {} non trouvé", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Mot de passe incorrect pour l'utilisateur {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }
        
        logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getUsername());
        // Ici, tu pourrais générer et renvoyer un token JWT pour sécuriser les autres endpoints
        return ResponseEntity.ok("Connexion réussie !");
    }
}
