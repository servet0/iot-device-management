package com.yourcompany.iotplatform.iot_device_management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.iotplatform.iot_device_management.model.Device;
import com.yourcompany.iotplatform.iot_device_management.model.TelemetryData;
import com.yourcompany.iotplatform.iot_device_management.repository.DeviceRepository;
import com.yourcompany.iotplatform.iot_device_management.repository.TelemetryDataRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MQTT Telemetri Servisi
 * MQTT üzerinden gelen telemetri verilerini alır, işler ve veritabanına kaydeder
 */
@Service
@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = false)
public class MqttTelemetryService {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttTelemetryService.class);
    
    @Autowired
    private MqttClient mqttClient;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private TelemetryDataRepository telemetryDataRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Value("${mqtt.telemetry.topic}")
    private String telemetryTopic;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    /**
     * Servis başlatıldığında MQTT topic'lerine abone olma
     */
    @PostConstruct
    public void init() {
        try {
            // Telemetri topic'ine abone olma
            mqttClient.subscribe(telemetryTopic, this::handleTelemetryMessage);
            logger.info("MQTT telemetri topic'ine abone olundu: {}", telemetryTopic);
            
        } catch (MqttException e) {
            logger.error("MQTT topic aboneliği hatası: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Servis kapatılırken kaynakları temizleme
     */
    @PreDestroy
    public void cleanup() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.unsubscribe(telemetryTopic);
                mqttClient.disconnect();
                logger.info("MQTT Client bağlantısı kapatıldı");
            }
        } catch (MqttException e) {
            logger.error("MQTT Client kapatma hatası: {}", e.getMessage(), e);
        }
        
        executorService.shutdown();
    }
    
    /**
     * MQTT telemetri mesajlarını işleme
     * @param topic MQTT topic
     * @param message MQTT mesajı
     */
    private void handleTelemetryMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            logger.debug("MQTT mesajı alındı - Topic: {}, Payload: {}", topic, payload);
            
            // Mesajı asenkron olarak işleme
            executorService.submit(() -> processTelemetryMessage(topic, payload));
            
        } catch (Exception e) {
            logger.error("MQTT mesaj işleme hatası: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Telemetri mesajını işleme ve veritabanına kaydetme
     * @param topic MQTT topic
     * @param payload Mesaj içeriği
     */
    private void processTelemetryMessage(String topic, String payload) {
        try {
            // Topic'ten cihaz ID'sini çıkarma (iot/{deviceId}/telemetry formatında)
            String deviceId = extractDeviceIdFromTopic(topic);
            if (deviceId == null) {
                logger.warn("Geçersiz topic formatı: {}", topic);
                return;
            }
            
            // Cihazı veritabanından bulma
            Optional<Device> deviceOpt = deviceRepository.findByDeviceId(deviceId);
            if (deviceOpt.isEmpty()) {
                logger.warn("Cihaz bulunamadı: {}", deviceId);
                return;
            }
            
            Device device = deviceOpt.get();
            
            // Cihazın son görülme zamanını güncelleme
            device.setLastSeen(LocalDateTime.now());
            device.setStatus(Device.DeviceStatus.ONLINE);
            deviceRepository.save(device);
            
            // JSON payload'u parse etme
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Telemetri verisini oluşturma
            TelemetryData telemetryData = new TelemetryData(device, topic, payload);
            
            // JSON'dan veri tipi ve değerleri çıkarma
            if (jsonNode.has("dataType")) {
                telemetryData.setDataType(jsonNode.get("dataType").asText());
            }
            
            if (jsonNode.has("unit")) {
                telemetryData.setUnit(jsonNode.get("unit").asText());
            }
            
            if (jsonNode.has("value")) {
                JsonNode valueNode = jsonNode.get("value");
                if (valueNode.isNumber()) {
                    telemetryData.setValueNumeric(valueNode.asDouble());
                } else if (valueNode.isTextual()) {
                    telemetryData.setValueString(valueNode.asText());
                } else if (valueNode.isBoolean()) {
                    telemetryData.setValueBoolean(valueNode.asBoolean());
                }
            }
            
            if (jsonNode.has("quality")) {
                telemetryData.setQuality(jsonNode.get("quality").asInt());
            }
            
            if (jsonNode.has("timestamp")) {
                try {
                    LocalDateTime timestamp = LocalDateTime.parse(jsonNode.get("timestamp").asText());
                    telemetryData.setTimestamp(timestamp);
                } catch (Exception e) {
                    logger.warn("Geçersiz timestamp formatı: {}", jsonNode.get("timestamp").asText());
                }
            }
            
            // Telemetri verisini veritabanına kaydetme
            telemetryDataRepository.save(telemetryData);
            
            // WebSocket üzerinden gerçek zamanlı veri gönderme
            sendRealtimeTelemetry(deviceId, telemetryData);
            
            logger.debug("Telemetri verisi kaydedildi - Cihaz: {}, Veri Tipi: {}", 
                    deviceId, telemetryData.getDataType());
            
        } catch (Exception e) {
            logger.error("Telemetri mesaj işleme hatası: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Topic'ten cihaz ID'sini çıkarma
     * @param topic MQTT topic (iot/{deviceId}/telemetry formatında)
     * @return Cihaz ID'si
     */
    private String extractDeviceIdFromTopic(String topic) {
        try {
            String[] parts = topic.split("/");
            if (parts.length >= 3 && "iot".equals(parts[0]) && "telemetry".equals(parts[2])) {
                return parts[1];
            }
        } catch (Exception e) {
            logger.warn("Topic parsing hatası: {}", topic);
        }
        return null;
    }
    
    /**
     * WebSocket üzerinden gerçek zamanlı telemetri verisi gönderme
     * @param deviceId Cihaz ID'si
     * @param telemetryData Telemetri verisi
     */
    private void sendRealtimeTelemetry(String deviceId, TelemetryData telemetryData) {
        try {
            // WebSocket topic'i: /topic/telemetry/{deviceId}
            String wsTopic = "/topic/telemetry/" + deviceId;
            
            // Telemetri verisini JSON formatında gönderme
            String jsonPayload = objectMapper.writeValueAsString(telemetryData);
            messagingTemplate.convertAndSend(wsTopic, jsonPayload);
            
            logger.debug("WebSocket üzerinden telemetri gönderildi: {}", wsTopic);
            
        } catch (Exception e) {
            logger.error("WebSocket telemetri gönderme hatası: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Manuel olarak telemetri verisi gönderme (test için)
     * @param deviceId Cihaz ID'si
     * @param payload Telemetri verisi
     */
    public void sendTelemetryData(String deviceId, String payload) {
        String topic = "iot/" + deviceId + "/telemetry";
        processTelemetryMessage(topic, payload);
    }
}
