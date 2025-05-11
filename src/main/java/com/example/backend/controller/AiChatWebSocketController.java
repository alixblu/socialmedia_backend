package com.example.backend.controller;

import com.example.backend.model.mongo.AiChatMessage;
import com.example.backend.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AiChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AiChatService aiChatService;
    
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
        
        if (userId == null || content == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Thiếu thông tin userId hoặc content");
            messagingTemplate.convertAndSend("/queue/ai." + botId + "." + userId, errorResponse);
            return;
        }
        
        // Process the user message and get bot response
        AiChatMessage botResponse = aiChatService.processUserMessage(userId, botId, content);
        
        // Create response payload
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("id", botResponse.getId());
        responsePayload.put("botId", botResponse.getBotId());
        responsePayload.put("content", botResponse.getContent());
        responsePayload.put("timestamp", botResponse.getTimestamp());
        responsePayload.put("role", "BOT");
        
        // Send the response to the specific user and bot queue
        messagingTemplate.convertAndSend("/queue/ai." + botId + "." + userId, responsePayload);
    }
} 