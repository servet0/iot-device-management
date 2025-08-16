package com.yourcompany.iotplatform.iot_device_management.exception;

/**
 * Kaynak Bulunamadı Exception'ı
 * İstenen kaynak bulunamadığında fırlatılır
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
