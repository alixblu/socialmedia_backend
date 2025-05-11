package com.example.backend.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Document(collection = "chat_messages")
public class ChatMessage {
    
    @Id
    private String id;
    
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private MessageStatus status;
    
    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
    
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
        this.status = MessageStatus.SENT;
    }
} 