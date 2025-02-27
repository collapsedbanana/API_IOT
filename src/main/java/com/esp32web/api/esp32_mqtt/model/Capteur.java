package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "capteurs")  // Nom de la table en BDD
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float temperature;
    private float humidity;
    private int luminositeRaw;
    private int humiditeSolRaw;
    private LocalDateTime timestamp;

    public Capteur() {}

    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminositeRaw = luminositeRaw;
        this.humiditeSolRaw = humiditeSolRaw;
        this.timestamp = LocalDateTime.now(); // Ajout automatique de la date/heure
    }

    // Getters et Setters
    public Long getId() { return id; }
    public float getTemperature() { return temperature; }
    public void setTemperature(float temperature) { this.temperature = temperature; }
    public float getHumidity() { return humidity; }
    public void setHumidity(float humidity) { this.humidity = humidity; }
    public int getLuminositeRaw() { return luminositeRaw; }
    public void setLuminositeRaw(int luminositeRaw) { this.luminositeRaw = luminositeRaw; }
    public int getHumiditeSolRaw() { return humiditeSolRaw; }
    public void setHumiditeSolRaw(int humiditeSolRaw) { this.humiditeSolRaw = humiditeSolRaw; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
