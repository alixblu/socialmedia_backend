package com.example.backend.controller;

import com.example.backend.model.mongo.ChatMessage;
import com.example.backend.repository.mongo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    /**
     * Handles chat messages
     * 
     * @param chatRoomId - The room ID
     * @param message - Message object containing sender, content, etc.
     * @return the processed message
     */
    @MessageMapping("/chat.sendMessage/{chatRoomId}")
    public void sendMessage(@DestinationVariable String chatRoomId, @Payload Map<String, Object> messagePayload) {
        String type = (String) messagePayload.get("type");
        
        // If it's a chat message, save it to the database
        if ("CHAT".equals(type)) {
            ChatMessage message = new ChatMessage();
            message.setChatRoomId(chatRoomId);
            message.setSenderId((Integer) messagePayload.get("senderId"));
            message.setReceiverId((Integer) messagePayload.get("receiverId"));
            message.setContent((String) messagePayload.get("content"));
            message.setTimestamp(LocalDateTime.now());
            message.setStatus(ChatMessage.MessageStatus.SENT);
            
            // Save to database
            ChatMessage savedMessage = chatMessageRepository.save(message);
            
            // Update the ID in the payload with the generated ID
            messagePayload.put("id", savedMessage.getId());
            messagePayload.put("timestamp", savedMessage.getTimestamp());
        }
        
        // Send to the specific chat room
        messagingTemplate.convertAndSend("/queue/chat." + chatRoomId, messagePayload);
    }
    
    /**
     * Handles typing indicator
     * 
     * @param chatRoomId - The room ID 
     * @param typingPayload - Contains user ID of who is typing
     */
    @MessageMapping("/chat.typing/{chatRoomId}")
    public void typingIndicator(@DestinationVariable String chatRoomId, @Payload Map<String, Object> typingPayload) {
        // Forward typing status to the specific chat room
        messagingTemplate.convertAndSend("/queue/chat." + chatRoomId, typingPayload);
    }
} 