package com.example.backend.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "chat_rooms")
public class ChatRoom {
    
    @Id
    private String id;
    
    private Integer friendshipId;
    private boolean active;
    
    public ChatRoom() {
        this.active = true;
    }
} 