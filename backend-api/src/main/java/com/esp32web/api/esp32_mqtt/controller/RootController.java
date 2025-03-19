package com.esp32web.api.esp32_mqtt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("✅ API ESP32 - Accès HTTPS sécurisé opérationnel !");
    }
}
