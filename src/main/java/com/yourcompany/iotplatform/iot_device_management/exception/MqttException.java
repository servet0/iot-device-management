package com.yourcompany.iotplatform.iot_device_management.exception;

/**
 * MQTT Exception'ı
 * MQTT bağlantı ve işlem hatalarında fırlatılır
 */
public class MqttException extends RuntimeException {
    
    public MqttException(String message) {
        super(message);
    }
    
    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }
}
