package com.esp32web.api.esp32_mqtt.service;

import com.esp32web.api.esp32_mqtt.model.Capteur;
import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledCapteurService {

    private final CapteurRepository capteurRepository;
    private final MqttService mqttService;

    public ScheduledCapteurService(CapteurRepository capteurRepository, MqttService mqttService) {
        this.capteurRepository = capteurRepository;
        this.mqttService = mqttService;
    }

    /**
     * Cette méthode s'exécute à la minute 0 de chaque heure (toutes les heures).
     * Cron : "0 0 * * * *" => minute=0, heure=toutes, jour=*, mois=*, etc.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void recordSensorData() {
        // On récupère la dernière donnée reçue via MQTT
        Capteur latest = mqttService.getLatestCapteur();
        if (latest != null) {
            // On crée un nouvel enregistrement basé sur la dernière donnée
            Capteur newRecord = new Capteur(
                    latest.getTemperature(),
                    latest.getHumidity(),
                    latest.getLuminositeRaw(),
                    latest.getHumiditeSolRaw()
            );
            capteurRepository.save(newRecord);
            System.out.println("✅ Données de capteur enregistrées à " + LocalDateTime.now());
        } else {
            System.out.println("❌ Aucune donnée de capteur disponible à " + LocalDateTime.now());
        }
    }
}
