package com.esp32web.api.esp32_mqtt.model;

import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final CapteurRepository capteurRepository;

    @Value("${mqtt.broker}")
    private String brokerUrl;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    private MqttClient client;

    public MqttService(CapteurRepository capteurRepository) {
        this.capteurRepository = capteurRepository;
    }

    @PostConstruct
    public void init() {
        // Affiche la valeur injectée pour le debug
        System.out.println("mqtt.clientId = " + clientId);
        connect();
    }

    public void connect() {
        try {
            if (client == null) {
                client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            }
    
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
    
            System.out.println("🔌 Tentative de connexion à MQTT : " + brokerUrl);
            client.connect(options);
            System.out.println("✅ Connexion MQTT réussie !");
    
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Connexion MQTT perdue : " + cause.getMessage());
                }
    
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("📥 Message reçu sur " + topic + " : " + new String(message.getPayload()));
                }
    
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("📡 Message MQTT envoyé avec succès.");
                }
            });
    
            client.subscribe(topic);
            System.out.println("✅ Abonné au topic : " + topic);
        } catch (MqttException e) {
            System.out.println("❌ Erreur de connexion MQTT : " + e.getMessage() + " (Code : " + e.getReasonCode() + ")");
        }
    }
    
}
