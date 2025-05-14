package com.example.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketHandshakeInterceptor handshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry messages back to the client
        // Messages whose destination starts with "/topic" or "/queue" will be routed to the broker
        config.enableSimpleBroker("/topic", "/queue");
        
        // Messages whose destination starts with "/app" will be routed to message-handling methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws-chat" endpoint, enabling SockJS fallback options
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:5174") // Allow your frontend origins
                .addInterceptors(handshakeInterceptor)
                .withSockJS(); // Enables SockJS fallback options
        
        // Also register the endpoint without SockJS for raw WebSocket connections
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:5174") // Allow your frontend origins
                .addInterceptors(handshakeInterceptor);
    }
} 