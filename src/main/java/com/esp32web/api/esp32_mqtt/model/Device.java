package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "devices")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le deviceId (ex: MAC address), unique pour identifier le "capteur physique"
    @Column(unique = true, nullable = false)
    private String deviceId;

    // Un nom lisible ou descriptif, si nécessaire
    private String name;

    // Association avec un utilisateur (désérialisé au frontend pour afficher à qui est lié ce capteur)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "role", "permission", "hibernateLazyInitializer", "handler"})
    private User user;

    // Liste des mesures associées à ce device
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"device"})  // Évite de renvoyer tout l'objet `device` dans chaque mesure
    private List<Measurement> measurements;

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
