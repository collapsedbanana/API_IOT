package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Utilisateur déjà existant";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Utilisateur enregistré avec succès";
    }
    
    // Vous pouvez ajouter un endpoint de login si vous utilisez JWT pour retourner un token
}
