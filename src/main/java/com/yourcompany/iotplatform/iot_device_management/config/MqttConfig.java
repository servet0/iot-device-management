package com.yourcompany.iotplatform.iot_device_management.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT Client konfigürasyonu
 * MQTT broker'a bağlantı ve client ayarları için kullanılır
 */
@Configuration
public class MqttConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);
    
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    @Value("${mqtt.client.id}")
    private String clientId;
    
    @Value("${mqtt.username:}")
    private String username;
    
    @Value("${mqtt.password:}")
    private String password;
    
    @Value("${mqtt.connection.timeout:30}")
    private int connectionTimeout;
    
    @Value("${mqtt.keep.alive.interval:60}")
    private int keepAliveInterval;
    
    /**
     * MQTT Client bean'i oluşturma
     * @return MQTT Client instance
     * @throws MqttException MQTT bağlantı hatası
     */
    @Bean
    public MqttClient mqttClient() throws MqttException {
        try {
            // Memory persistence kullanarak client oluşturma
            MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            
            // Bağlantı seçeneklerini ayarlama
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(connectionTimeout);
            options.setKeepAliveInterval(keepAliveInterval);
            options.setAutomaticReconnect(true);
            
            // Kullanıcı adı ve şifre varsa ayarlama
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
                if (password != null && !password.isEmpty()) {
                    options.setPassword(password.toCharArray());
                }
            }
            
            // Bağlantıyı kurma
            client.connect(options);
            
            logger.info("MQTT Client başarıyla bağlandı: {}", brokerUrl);
            
            return client;
            
        } catch (MqttException e) {
            logger.error("MQTT Client bağlantı hatası: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * MQTT Bağlantı seçenekleri bean'i
     * @return MQTT Connect Options
     */
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);
        options.setAutomaticReconnect(true);
        
        if (username != null && !username.isEmpty()) {
            options.setUserName(username);
            if (password != null && !password.isEmpty()) {
                options.setPassword(password.toCharArray());
            }
        }
        
        return options;
    }
}
