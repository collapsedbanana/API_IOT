package com.esp32web.api.esp32_mqtt.dto;

import java.time.LocalDateTime;

public class MeasurementDTO {
    private Float temperature;
    private Float humidity;
    private Integer luminositeRaw;
    private Integer humiditeSolRaw;
    private LocalDateTime timestamp;
    private String deviceId;  // Identifiant du dispositif (ex: MAC address)

    public MeasurementDTO() {}

    // Getters et setters
    public Float getTemperature() {
        return temperature;
    }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }
    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public Integer getLuminositeRaw() {
        return luminositeRaw;
    }
    public void setLuminositeRaw(Integer luminositeRaw) {
        this.luminositeRaw = luminositeRaw;
    }

    public Integer getHumiditeSolRaw() {
        return humiditeSolRaw;
    }
    public void setHumiditeSolRaw(Integer humiditeSolRaw) {
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
}
