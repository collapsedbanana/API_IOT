package com.esp32web.api.esp32_mqtt.model;

import com.esp32web.api.esp32_mqtt.repository.CapteurRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        System.out.println("📡 Initialisation MQTT avec clientId : " + clientId);
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
                    System.err.println("❌ Connexion MQTT perdue : " + cause.getMessage());
                    reconnect();
                }

                @Override
                @Transactional
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("📥 Message reçu sur " + topic + " : " + payload);

                    try {
                        JSONObject json = new JSONObject(payload);
                        float temperature = (float) json.getDouble("temperature");
                        float humidity = (float) json.getDouble("humidity");
                        int luminositeRaw = json.getInt("luminosite_raw");
                        int humiditeSolRaw = json.getInt("humidite_sol_raw");

                        System.out.println("🌡 Température : " + temperature);
                        System.out.println("💧 Humidité : " + humidity);
                        System.out.println("🔆 Luminosité : " + luminositeRaw);
                        System.out.println("🌱 Humidité du sol : " + humiditeSolRaw);

                        // Sauvegarde en base de données
                        Capteur capteur = new Capteur(temperature, humidity, luminositeRaw, humiditeSolRaw);
                        capteurRepository.save(capteur);
                        System.out.println("✅ Données enregistrées en base !");
                    } catch (Exception e) {
                        System.err.println("⚠️ Erreur lors du traitement du message MQTT : " + e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("📡 Message MQTT envoyé avec succès.");
                }
            });

            client.subscribe(topic);
            System.out.println("✅ Abonné au topic : " + topic);
        } catch (MqttException e) {
            System.err.println("❌ Erreur de connexion MQTT : " + e.getMessage() + " (Code : " + e.getReasonCode() + ")");
        }
    }

    private void reconnect() {
        while (!client.isConnected()) {
            try {
                System.out.println("🔄 Tentative de reconnexion à MQTT...");
                client.reconnect();
                System.out.println("✅ Reconnexion réussie !");
            } catch (MqttException e) {
                System.err.println("⚠️ Erreur de reconnexion MQTT : " + e.getMessage());
                try {
                    Thread.sleep(5000); // Attente avant la prochaine tentative
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
