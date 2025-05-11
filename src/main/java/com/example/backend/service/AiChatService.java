package com.example.backend.service;

import com.example.backend.model.AiBot;
import com.example.backend.model.mongo.AiChatMessage;
import com.example.backend.model.mongo.AiChatMessage.MessageRole;
import com.example.backend.repository.mongo.AiChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    @Autowired
    private AiChatMessageRepository chatMessageRepository;
    
    @Autowired
    private MyOpenAiService openAiService;
    
    private final Map<String, AiBot> bots = new ConcurrentHashMap<>();
    
    /**
     * Initialize bots on service creation
     */
    public AiChatService() {
        // Spring Bot
        AiBot springBot = new AiBot("spring-bot", "Spring Bot", "default-avatar.png", "Trợ giúp về Spring Boot");
        springBot.addCapability("Spring Boot");
        springBot.addCapability("REST API");
        springBot.addCapability("Spring Security");
        springBot.addCapability("Spring Data");
        springBot.addSuggestedPrompt("Tạo REST API với Spring Boot");
        springBot.addSuggestedPrompt("Giải thích Spring Security");
        springBot.addSuggestedPrompt("Làm việc với Spring Data JPA");
        bots.put(springBot.getId(), springBot);
        
        // Java Bot
        AiBot javaBot = new AiBot("java-bot", "Java Helper", "default-avatar.png", "Giải đáp vấn đề Java");
        javaBot.addCapability("Java Core");
        javaBot.addCapability("Java Collections");
        javaBot.addCapability("Java Streams");
        javaBot.addCapability("Multithreading");
        javaBot.addSuggestedPrompt("Giải thích về Java Streams");
        javaBot.addSuggestedPrompt("Cách sử dụng Lambda trong Java");
        javaBot.addSuggestedPrompt("Lập trình đa luồng trong Java");
        bots.put(javaBot.getId(), javaBot);
    }
    
    /**
     * Get all available bots
     */
    public List<AiBot> getAllBots() {
        return new ArrayList<>(bots.values());
    }
    
    /**
     * Get a specific bot by ID
     */
    public Optional<AiBot> getBotById(String botId) {
        return Optional.ofNullable(bots.get(botId));
    }
    
    /**
     * Process a user message and generate a bot response
     */
    public AiChatMessage processUserMessage(Integer userId, String botId, String userMessage) {
        // Save user message
        AiChatMessage userChatMessage = new AiChatMessage(botId, userId, userMessage, MessageRole.USER);
        chatMessageRepository.save(userChatMessage);
        
        // Get chat history for context
        List<AiChatMessage> chatHistory = chatMessageRepository.findByUserIdAndBotIdOrderByTimestampAsc(userId, botId);
        
        // Get bot role description
        String botRole = getBotRoleDescription(botId);
        
        // Generate bot response using OpenAI
        String botResponse = openAiService.generateResponse(botRole, userMessage, chatHistory);
        
        // Save bot response
        AiChatMessage botChatMessage = new AiChatMessage(botId, userId, botResponse, MessageRole.BOT);
        chatMessageRepository.save(botChatMessage);
        
        return botChatMessage;
    }
    
    /**
     * Save a message to the database
     */
    public AiChatMessage saveMessage(AiChatMessage message) {
        return chatMessageRepository.save(message);
    }
    
    /**
     * Get chat history for a specific user and bot
     */
    public List<AiChatMessage> getChatHistory(Integer userId, String botId) {
        return chatMessageRepository.findByUserIdAndBotIdOrderByTimestampAsc(userId, botId);
    }
    
    /**
     * Get recent conversations for a user across all bots
     */
    public List<Map<String, Object>> getRecentConversations(Integer userId) {
        // Find all unique botIds for this user
        List<String> uniqueBotIds = chatMessageRepository.findDistinctBotIdByUserId(userId)
                .stream()
                .map(AiChatMessage::getBotId)
                .distinct()
                .collect(Collectors.toList());
        
        List<Map<String, Object>> recentConversations = new ArrayList<>();
        
        for (String botId : uniqueBotIds) {
            // Get latest message for each bot
            List<AiChatMessage> latestMessages = chatMessageRepository.findTop20ByUserIdAndBotIdOrderByTimestampDesc(userId, botId);
            
            if (!latestMessages.isEmpty()) {
                AiChatMessage latestMessage = latestMessages.get(0);
                AiBot bot = bots.get(botId);
                
                if (bot != null) {
                    Map<String, Object> conversation = new HashMap<>();
                    conversation.put("botId", botId);
                    conversation.put("botName", bot.getName());
                    conversation.put("botAvatar", bot.getAvatarUrl());
                    conversation.put("lastMessage", latestMessage.getContent());
                    conversation.put("timestamp", latestMessage.getTimestamp());
                    
                    recentConversations.add(conversation);
                }
            }
        }
        
        // Sort by timestamp descending
        recentConversations.sort((c1, c2) -> ((java.time.LocalDateTime)c2.get("timestamp"))
                .compareTo((java.time.LocalDateTime)c1.get("timestamp")));
        
        return recentConversations;
    }
    
    /**
     * Get bot role description for system prompt
     */
    public String getBotRoleDescription(String botId) {
        AiBot bot = bots.get(botId);
        if (bot == null) {
            return "Bạn là một trợ lý AI hữu ích.";
        }
        
        // Spring Bot
        if ("spring-bot".equals(botId)) {
            return "Bạn là một chuyên gia về Spring Framework và Spring Boot. " +
                   "Nhiệm vụ của bạn là cung cấp hướng dẫn, giải thích và ví dụ code về Spring Boot, " +
                   "cách tạo REST API, cấu hình Spring Security, và làm việc với Spring Data JPA. " +
                   "Tập trung vào việc cung cấp code ví dụ thực tế và ngắn gọn, dễ hiểu. " +
                   "Trả lời bằng tiếng Việt. Nếu cần cung cấp code, sử dụng cú pháp markdown với ```java và ```";
        }
        
        // Java Bot
        else if ("java-bot".equals(botId)) {
            return "Bạn là một chuyên gia Java. " +
                   "Nhiệm vụ của bạn là cung cấp hướng dẫn, giải thích và ví dụ code về Java Core, " +
                   "Collections, Streams, Lambda expressions, và lập trình đa luồng. " +
                   "Tập trung vào việc cung cấp code ví dụ thực tế và ngắn gọn, dễ hiểu. " +
                   "Trả lời bằng tiếng Việt. Nếu cần cung cấp code, sử dụng cú pháp markdown với ```java và ```";
        }
        
        // Default role
        return "Bạn là một trợ lý AI hữu ích tên " + bot.getName() + ". " +
               "Nhiệm vụ của bạn là cung cấp thông tin và hỗ trợ về: " + 
               String.join(", ", bot.getCapabilities()) + ". " +
               "Trả lời bằng tiếng Việt, súc tích và dễ hiểu.";
    }
} 