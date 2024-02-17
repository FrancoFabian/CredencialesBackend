package com.mx.credenciales.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.lang.annotation.Annotation;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra un endpoint de WebSocket y habilita SockJS como fallback para navegadores que no soportan WebSocket
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Usa el prefijo "/app" para mensajes dirigidos a métodos anotados con @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        // Usa "/topic" como prefijo para los nombres de los topics a los que se pueden suscribir los clientes
        registry.enableSimpleBroker("/topic");
    }

}
