package com.esp32web.api.esp32_mqtt.service;

import com.esp32web.api.esp32_mqtt.model.Device;
import com.esp32web.api.esp32_mqtt.model.Measurement;
import com.esp32web.api.esp32_mqtt.repository.DeviceRepository;
import com.esp32web.api.esp32_mqtt.repository.MeasurementRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final DeviceRepository deviceRepository;
    private final MeasurementRepository measurementRepository;

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

    public MqttService(DeviceRepository deviceRepository, MeasurementRepository measurementRepository) {
        this.deviceRepository = deviceRepository;
        this.measurementRepository = measurementRepository;
    }

    @PostConstruct
    public void init() {
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

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("🔌 Connexion MQTT perdue : " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("📥 Message reçu : " + payload);
                    onMessageReceived(payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("📡 Message MQTT envoyé avec succès.");
                }
            });

            if (!client.isConnected()) {
                client.connect(options);
            }
            client.subscribe(topic);
            System.out.println("✅ Connecté à MQTT et abonné au topic : " + topic);
        } catch (MqttException e) {
            System.out.println("❌ Erreur de connexion MQTT : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onMessageReceived(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            float temperature = (float) json.getDouble("temperature");
            float humidity = (float) json.getDouble("humidity");
            int luminositeRaw = json.getInt("luminosite_raw");
            int humiditeSolRaw = json.getInt("humidite_sol_raw");
            String macAddress = json.optString("macAddress", null);

            if (macAddress == null || macAddress.isEmpty()) {
                throw new IllegalArgumentException("Adresse MAC manquante ou invalide !");
            }

            // 1) Récupérer ou créer le device correspondant au macAddress
            Device device = deviceRepository.findByDeviceId(macAddress);
            if (device == null) {
                device = new Device();
                device.setDeviceId(macAddress);
                deviceRepository.save(device);
            }

            // 2) Créer et sauvegarder la mesure
            Measurement measurement = new Measurement();
            measurement.setTemperature(temperature);
            measurement.setHumidity(humidity);
            measurement.setLuminositeRaw(luminositeRaw);
            measurement.setHumiditeSolRaw(humiditeSolRaw);
            measurement.setDevice(device);

            measurementRepository.save(measurement);

            System.out.println("✅ Mesure enregistrée pour le device: " + macAddress);
        } catch (Exception e) {
            System.out.println("⚠️ Erreur lors du traitement du message : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
