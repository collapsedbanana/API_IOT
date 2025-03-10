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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Changement ici : le mapping est "/create" pour éviter toute ambiguïté
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        logger.info("Création d'un nouvel utilisateur par l'admin: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Utilisateur {} déjà existant", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Utilisateur déjà existant");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Par défaut, si aucun rôle n'est spécifié, on définit "USER"
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        // Créer et associer des permissions par défaut
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
    // Récupérer l'utilisateur par son nom
    User user = userRepository.findByUsername(username);
    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable : " + username);
    }

    // Si l'utilisateur n'a pas encore de permission, on la crée
    UserPermission userPermission = user.getPermission();
    if (userPermission == null) {
        userPermission = new UserPermission();
        userPermission.setUser(user);
        user.setPermission(userPermission);
    }

    // Mettre à jour les permissions depuis le DTO
    userPermission.setCanViewTemperature(permissionDTO.isCanViewTemperature());
    userPermission.setCanViewHumidity(permissionDTO.isCanViewHumidity());
    userPermission.setCanViewLuminosite(permissionDTO.isCanViewLuminosite());
    userPermission.setCanViewHumiditeSol(permissionDTO.isCanViewHumiditeSol());

    // Sauvegarder
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
}
