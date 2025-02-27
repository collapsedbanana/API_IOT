package com.esp32web.api.esp32_mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.esp32web.api.esp32_mqtt")  // 🔹 Force Spring à scanner tous les packages
public class Esp32MqttApplication {
    public static void main(String[] args) {
        SpringApplication.run(Esp32MqttApplication.class, args);
    }
}
