package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.dto.PermissionDTO;
import com.esp32web.api.esp32_mqtt.model.User;
import com.esp32web.api.esp32_mqtt.model.UserPermission;
import com.esp32web.api.esp32_mqtt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')") // 🔒 Tous les endpoints de ce contrôleur nécessitent le rôle ADMIN
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Création d'un nouvel utilisateur par l'admin: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Utilisateur {} déjà existant", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilisateur déjà existant");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        UserPermission permission = new UserPermission();
        permission.setUser(user);
        permission.setCanViewTemperature(true);
        permission.setCanViewLuminosite(true);
        permission.setCanViewHumidity(false);
        permission.setCanViewHumiditeSol(false);
        user.setPermission(permission);

        userRepository.save(user);
        logger.info("Utilisateur {} créé avec succès par l'admin", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur créé avec succès");
    }

    @PutMapping("/update-permissions/{username}")
    public ResponseEntity<?> updateUserPermissions(@PathVariable String username,
                                                   @RequestBody PermissionDTO permissionDTO) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable : " + username);
        }

        UserPermission userPermission = user.getPermission();
        if (userPermission == null) {
            userPermission = new UserPermission();
            userPermission.setUser(user);
            user.setPermission(userPermission);
        }

        userPermission.setCanViewTemperature(permissionDTO.isCanViewTemperature());
        userPermission.setCanViewHumidity(permissionDTO.isCanViewHumidity());
        userPermission.setCanViewLuminosite(permissionDTO.isCanViewLuminosite());
        userPermission.setCanViewHumiditeSol(permissionDTO.isCanViewHumiditeSol());

        userRepository.save(user);

        return ResponseEntity.ok("Permissions mises à jour pour l'utilisateur " + username);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable : " + username);
        }
        userRepository.delete(user);
        return ResponseEntity.ok("Utilisateur " + username + " supprimé avec succès");
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
