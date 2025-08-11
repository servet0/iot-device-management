package com.yourcompany.iotplatform.iot_device_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Konfigürasyonu
 * STOMP protokolü ile gerçek zamanlı mesajlaşma için kullanılır
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Value("${websocket.endpoint:/ws}")
    private String websocketEndpoint;
    
    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;
    
    /**
     * STOMP endpoint'lerini konfigüre etme
     * @param registry STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS(); // SockJS desteği ekleme
    }
    
    /**
     * Message broker konfigürasyonu
     * @param registry Message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Client'ların subscribe olabileceği destination prefix'leri
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Client'lardan gelen mesajların destination prefix'i
        registry.setApplicationDestinationPrefixes("/app");
        
        // User destination prefix'i (kullanıcıya özel mesajlar için)
        registry.setUserDestinationPrefix("/user");
    }
}
