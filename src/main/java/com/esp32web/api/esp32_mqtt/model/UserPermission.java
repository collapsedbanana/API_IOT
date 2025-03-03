package com.esp32web.api.esp32_mqtt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_permissions")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Association One-to-One avec User
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "can_view_temperature")
    private boolean canViewTemperature;

    @Column(name = "can_view_humidity")
    private boolean canViewHumidity;

    @Column(name = "can_view_luminosite")
    private boolean canViewLuminosite;

    @Column(name = "can_view_humidite_sol")
    private boolean canViewHumiditeSol;

    public UserPermission() {}

    public UserPermission(User user, boolean canViewTemperature, boolean canViewHumidity,
                          boolean canViewLuminosite, boolean canViewHumiditeSol) {
        this.user = user;
        this.canViewTemperature = canViewTemperature;
        this.canViewHumidity = canViewHumidity;
        this.canViewLuminosite = canViewLuminosite;
        this.canViewHumiditeSol = canViewHumiditeSol;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public boolean isCanViewTemperature() {
        return canViewTemperature;
    }
    public void setCanViewTemperature(boolean canViewTemperature) {
        this.canViewTemperature = canViewTemperature;
    }
    public boolean isCanViewHumidity() {
        return canViewHumidity;
    }
    public void setCanViewHumidity(boolean canViewHumidity) {
        this.canViewHumidity = canViewHumidity;
    }
    public boolean isCanViewLuminosite() {
        return canViewLuminosite;
    }
    public void setCanViewLuminosite(boolean canViewLuminosite) {
        this.canViewLuminosite = canViewLuminosite;
    }
    public boolean isCanViewHumiditeSol() {
        return canViewHumiditeSol;
    }
    public void setCanViewHumiditeSol(boolean canViewHumiditeSol) {
        this.canViewHumiditeSol = canViewHumiditeSol;
    }
}
