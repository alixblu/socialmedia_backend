package com.example.backend.service;

import com.example.backend.model.AiBot;
import com.example.backend.model.mongo.AiChatMessage;
import com.example.backend.repository.AiBotRepository;
import com.example.backend.repository.mongo.AiChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatService {
    @Autowired
    private AiBotRepository aiBotRepository;
    
    @Autowired
    private AiChatMessageRepository aiChatMessageRepository;

    public List<AiBot> getAllBots() {
        return aiBotRepository.findAll();
    }
    
    public Optional<AiBot> getBotById(String botId) {
        return aiBotRepository.findById(botId);
    }
    
    public List<AiChatMessage> getChatHistory(Integer userId, String botId) {
        return aiChatMessageRepository.findByUserIdAndBotIdOrderByTimestampAsc(userId, botId);
    }
    
    public List<Map<String, Object>> getRecentConversations(Integer userId) {
        List<AiChatMessage> messages = aiChatMessageRepository.findByUserIdOrderByTimestampDesc(userId);
        
        Map<String, Map<String, Object>> conversations = new HashMap<>();
        
        for (AiChatMessage message : messages) {
            String botId = message.getBotId();
            if (!conversations.containsKey(botId)) {
                Optional<AiBot> bot = aiBotRepository.findById(botId);
                if (bot.isPresent()) {
                    Map<String, Object> conversation = new HashMap<>();
                    conversation.put("botId", botId);
                    conversation.put("botName", bot.get().getName());
                    conversation.put("lastMessage", message.getContent());
                    conversation.put("timestamp", message.getTimestamp());
                    conversations.put(botId, conversation);
                }
            }
        }
        
        return new ArrayList<>(conversations.values());
    }
    
    public AiChatMessage processUserMessage(Integer userId, String botId, String userMessage) {
        // Get chat history for context
        List<AiChatMessage> chatHistory = getChatHistory(userId, botId);
        
        // Save user message
        AiChatMessage userChatMessage = new AiChatMessage(botId, userId, userMessage, AiChatMessage.MessageRole.USER);
        AiChatMessage savedUserMessage = saveMessage(userChatMessage);
        
        // Get bot role description
        String botRole = getBotRoleDescription(botId);
        
        // Generate bot response using Rasa
        String botResponse = "This message should be handled by Rasa service.";
        
        // Save bot response
        AiChatMessage botChatMessage = new AiChatMessage(botId, userId, botResponse, AiChatMessage.MessageRole.BOT);
        return saveMessage(botChatMessage);
    }
    
    public AiChatMessage saveMessage(AiChatMessage message) {
        return aiChatMessageRepository.save(message);
    }
    
    public String getBotRoleDescription(String botId) {
        return aiBotRepository.findById(botId)
                .map(AiBot::getDescription)
                .orElse("A helpful AI assistant");
    }
} 