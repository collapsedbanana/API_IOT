package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le deviceId (ex: MAC address), unique pour identifier le "capteur physique"
    @Column(unique = true, nullable = false)
    private String deviceId;

    // Un nom lisible ou descriptif, si nécessaire
    private String name;

    // Si vous voulez assigner un device à un utilisateur
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    private User user;

    // Liste des mesures associées à ce device
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements;

    // Constructeurs par défaut et personnalisés
    public Device() {}

    public Device(String deviceId, String name, User user) {
        this.deviceId = deviceId;
        this.name = name;
        this.user = user;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}
