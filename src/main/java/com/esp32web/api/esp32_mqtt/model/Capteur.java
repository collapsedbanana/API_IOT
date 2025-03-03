package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    // Nouvelle colonne pour identifier le dispositif/capteur
    @Column(name = "device_id")
    private String deviceId;

    // Association vers l'utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Capteur() {}

    // Constructeur sans utilisateur (pour des enregistrements initiaux)
    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminositeRaw = luminositeRaw;
        this.humiditeSolRaw = humiditeSolRaw;
        this.timestamp = LocalDateTime.now();
    }

    // Constructeur incluant l'utilisateur
    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw, User user) {
        this(temperature, humidity, luminositeRaw, humiditeSolRaw);
        this.user = user;
    }
    
    // Constructeur incluant l'utilisateur et le deviceId
    public Capteur(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw, String deviceId, User user) {
        this(temperature, humidity, luminositeRaw, humiditeSolRaw, user);
        this.deviceId = deviceId;
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
