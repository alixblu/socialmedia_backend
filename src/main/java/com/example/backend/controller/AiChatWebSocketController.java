package com.example.backend.controller;


import com.example.backend.service.RasaAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class AiChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    
    @Autowired(required = false)
    private RasaAiService rasaAiService;
    
    /**
     * Handle AI chat messages via WebSocket
     * 
     * @param botId - The AI bot ID
     * @param message - Message payload containing userId and content
     */
    @MessageMapping("/ai.chat/{botId}")
    public void handleAiChat(@DestinationVariable String botId, @Payload Map<String, Object> message) {
        Integer userId = (Integer) message.get("userId");
        String content = (String) message.get("content");
        
        if (userId == null || content == null || rasaAiService == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Thiếu thông tin userId hoặc content hoặc Rasa service không khả dụng");
            messagingTemplate.convertAndSend("/queue/ai." + botId + "." + userId, errorResponse);
            return;
        }
        
        // Get bot role and generate response using Rasa
        String botRole = "A helpful AI assistant";
        String rasaResponse = rasaAiService.generateResponse(botRole, content);
        
        // Create response payload
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("id", UUID.randomUUID().toString());
        responsePayload.put("botId", botId);
        responsePayload.put("content", rasaResponse);
        responsePayload.put("timestamp", System.currentTimeMillis());
        responsePayload.put("role", "BOT");
        
        // Send the response to the specific user and bot queue
        messagingTemplate.convertAndSend("/queue/ai." + botId + "." + userId, responsePayload);
    }
} 