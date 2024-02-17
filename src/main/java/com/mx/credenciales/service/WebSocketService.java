package com.mx.credenciales.service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void sendProgressUpdate(String topic, int progress) {
        // Asegúrate de enviar un objeto JSON con la clave "progress"
        String jsonMessage = String.format("{\"type\":\"PROGRESS\", \"progress\":%d}", progress);
        messagingTemplate.convertAndSend(topic, jsonMessage);
    }

}
