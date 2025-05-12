package com.example.backend.controller;

import com.example.backend.model.AiBot;
import com.example.backend.service.AiChatService;
import com.example.backend.service.RasaAiService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Send a message to a bot and get a response using Rasa
     */
    @PostMapping("/rasa/chat")
    public ResponseEntity<Map<String, Object>> sendRasaMessage(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String botId = (String) request.get("botId");
        String message = (String) request.get("message");
        
        if (userId == null || botId == null || message == null || rasaAiService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Get bot role description
        String botRole = aiChatService.getBotRoleDescription(botId);
        
        // Generate response using Rasa (without chat history)
        String rasaResponse = rasaAiService.generateResponse(botRole, message);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", java.util.UUID.randomUUID().toString());
        result.put("botId", botId);
        result.put("content", rasaResponse);
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
} 