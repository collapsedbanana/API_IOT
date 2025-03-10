package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "capteurs")
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float temperature;
    private float humidity;
    private int luminositeRaw;
    private int humiditeSolRaw;
    private LocalDateTime timestamp;

    @Column(name = "device_id")
    private String deviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Capteur() {
        // Constructeur par défaut
    }

    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw, String deviceId, User user) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminositeRaw = luminositeRaw;
        this.humiditeSolRaw = humiditeSolRaw;
        this.timestamp = LocalDateTime.now();
        this.deviceId = deviceId;
        this.user = user;
    }

    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminositeRaw = luminositeRaw;
        this.humiditeSolRaw = humiditeSolRaw;
        this.timestamp = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public int getLuminositeRaw() {
        return luminositeRaw;
    }

    public void setLuminositeRaw(int luminositeRaw) {
        this.luminositeRaw = luminositeRaw;
    }

    public int getHumiditeSolRaw() {
        return humiditeSolRaw;
    }

    public void setHumiditeSolRaw(int humiditeSolRaw) {
        this.humiditeSolRaw = humiditeSolRaw;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
