package com.esp32web.api.esp32_mqtt.dto;

public class PermissionDTO {
    private boolean canViewTemperature;
    private boolean canViewHumidity;
    private boolean canViewLuminosite;
    private boolean canViewHumiditeSol;

    // Getters et Setters
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
