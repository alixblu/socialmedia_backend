package com.example.backend.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    private String id;
    private String chatRoomId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime timestamp;
    private MessageType type;
    
    public enum MessageType {
        CHAT,      // Regular chat message
        JOIN,      // User joined
        LEAVE,     // User left
        TYPING,    // User is typing
        DELIVERED, // Message delivered
        READ       // Message read
    }
    
    // Constructor for typing status messages
    public static ChatMessage createTypingMessage(String chatRoomId, Integer senderId, Integer receiverId, boolean isTyping) {
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setTimestamp(LocalDateTime.now());
        message.setType(isTyping ? MessageType.TYPING : MessageType.CHAT);
        return message;
    }
} 