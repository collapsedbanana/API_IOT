package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "measurements")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float temperature;
    private float humidity;
    private int luminositeRaw;
    private int humiditeSolRaw;
    private LocalDateTime timestamp = LocalDateTime.now();

    // Association à un "Device" (capteur physique)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id") 
    @JsonIgnore // On ne sérialise pas le device complet pour éviter la récursivité
    private Device device;

    public Measurement() {}

    public Measurement(float temperature, float humidity, int luminositeRaw, int humiditeSolRaw, Device device) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminositeRaw = luminositeRaw;
        this.humiditeSolRaw = humiditeSolRaw;
        this.device = device;
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
    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }
}
