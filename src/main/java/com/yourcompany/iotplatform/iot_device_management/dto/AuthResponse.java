package com.yourcompany.iotplatform.iot_device_management.dto;

import com.yourcompany.iotplatform.iot_device_management.model.User;

/**
 * Kimlik Doğrulama Yanıt DTO'su
 * Login ve register işlemlerinde dönen yanıt için kullanılır
 */
public class AuthResponse {
    
    private String message;
    private String token;
    private User user;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String message, String token, User user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
