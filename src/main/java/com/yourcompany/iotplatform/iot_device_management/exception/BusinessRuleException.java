package com.yourcompany.iotplatform.iot_device_management.exception;

/**
 * İş Kuralı Exception'ı
 * İş kuralları ihlal edildiğinde fırlatılır
 */
public class BusinessRuleException extends RuntimeException {
    
    public BusinessRuleException(String message) {
        super(message);
    }
    
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
