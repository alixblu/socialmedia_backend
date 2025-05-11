package com.example.backend.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ai_chat_messages")
public class AiChatMessage {
    
    @Id
    private String id;
    private String botId;
    private Integer userId;
    private String content;
    private MessageRole role; // USER or BOT
    private LocalDateTime timestamp;
    
    public enum MessageRole {
        USER, BOT
    }
    
    // Constructors
    public AiChatMessage() {
    }
    
    public AiChatMessage(String botId, Integer userId, String content, MessageRole role) {
        this.botId = botId;
        this.userId = userId;
        this.content = content;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getBotId() {
        return botId;
    }
    
    public void setBotId(String botId) {
        this.botId = botId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public MessageRole getRole() {
        return role;
    }
    
    public void setRole(MessageRole role) {
        this.role = role;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 