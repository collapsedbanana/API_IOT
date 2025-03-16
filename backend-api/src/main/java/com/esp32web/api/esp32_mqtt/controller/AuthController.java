package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import com.esp32web.api.esp32_mqtt.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

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
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Identifiants invalides pour {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nom d'utilisateur ou mot de passe incorrect");
        }
    
        String token = jwtService.generateToken(user);
    
        return ResponseEntity.ok(Map.of(
            "token", token,
            "username", user.getUsername(),
            "role", user.getRole()
        ));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "role", user.getRole()
        ));
    }
}
