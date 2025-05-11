package com.example.backend.controller;

import com.example.backend.model.AiBot;
import com.example.backend.model.mongo.AiChatMessage;
import com.example.backend.service.AiChatService;
import com.example.backend.service.RasaAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;
    
    @Autowired(required = false)
    private RasaAiService rasaAiService;
    
    /**
     * Get all available bots
     */
    @GetMapping("/bots")
    public ResponseEntity<List<AiBot>> getAllBots() {
        return ResponseEntity.ok(aiChatService.getAllBots());
    }
    
    /**
     * Get a specific bot by ID
     */
    @GetMapping("/bots/{botId}")
    public ResponseEntity<?> getBotById(@PathVariable String botId) {
        return aiChatService.getBotById(botId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get chat history for a specific user and bot
     */
    @GetMapping("/history/{userId}/{botId}")
    public ResponseEntity<List<AiChatMessage>> getChatHistory(
            @PathVariable Integer userId,
            @PathVariable String botId) {
        return ResponseEntity.ok(aiChatService.getChatHistory(userId, botId));
    }
    
    /**
     * Get recent conversations for a user across all bots
     */
    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getRecentConversations(@PathVariable Integer userId) {
        return ResponseEntity.ok(aiChatService.getRecentConversations(userId));
    }
    
    /**
     * Send a message to a bot and get a response (OpenAI)
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String botId = (String) request.get("botId");
        String message = (String) request.get("message");
        
        if (userId == null || botId == null || message == null) {
            return ResponseEntity.badRequest().build();
        }
        
        AiChatMessage response = aiChatService.processUserMessage(userId, botId, message);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", response.getId());
        result.put("botId", response.getBotId());
        result.put("content", response.getContent());
        result.put("timestamp", response.getTimestamp());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Send a message to a bot and get a response using Rasa
     * This endpoint is only available when the rasa profile is active
     */
    @PostMapping("/rasa/chat")
    public ResponseEntity<Map<String, Object>> sendRasaMessage(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String botId = (String) request.get("botId");
        String message = (String) request.get("message");
        
        if (userId == null || botId == null || message == null || rasaAiService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Get chat history for context
        List<AiChatMessage> chatHistory = aiChatService.getChatHistory(userId, botId);
        
        // Save user message
        AiChatMessage userChatMessage = new AiChatMessage(botId, userId, message, AiChatMessage.MessageRole.USER);
        AiChatMessage savedUserMessage = aiChatService.saveMessage(userChatMessage);
        
        // Get bot role description
        String botRole = aiChatService.getBotRoleDescription(botId);
        
        // Generate response using Rasa
        String rasaResponse = rasaAiService.generateResponse(botRole, message, chatHistory);
        
        // Save Rasa response
        AiChatMessage botChatMessage = new AiChatMessage(botId, userId, rasaResponse, AiChatMessage.MessageRole.BOT);
        AiChatMessage savedBotMessage = aiChatService.saveMessage(botChatMessage);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedBotMessage.getId());
        result.put("botId", savedBotMessage.getBotId());
        result.put("content", savedBotMessage.getContent());
        result.put("timestamp", savedBotMessage.getTimestamp());
        
        return ResponseEntity.ok(result);
    }
} 