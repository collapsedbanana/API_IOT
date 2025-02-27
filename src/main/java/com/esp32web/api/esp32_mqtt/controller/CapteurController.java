package com.esp32web.api.esp32_mqtt.controller;

import com.esp32web.api.esp32_mqtt.model.Capteur;
import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/capteurs")
public class CapteurController {

    private final CapteurRepository capteurRepository;

    public CapteurController(CapteurRepository capteurRepository) {
        this.capteurRepository = capteurRepository;
    }

    @GetMapping
    public List<Capteur> getAllCapteurs() {
        return capteurRepository.findAll();
    }
}
