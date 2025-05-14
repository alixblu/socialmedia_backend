package com.example.backend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleWebSocketHandler extends TextWebSocketHandler {
    
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("New connection established: " + sessionId);
        
        // Send welcome message
        session.sendMessage(new TextMessage("Welcome! Your session ID is: " + sessionId));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message from " + session.getId() + ": " + payload);
        
        // Echo the message back to the sender
        session.sendMessage(new TextMessage("Echo: " + payload));
        
        // Broadcast to all other connected clients
        broadcastMessage(session.getId(), payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        System.out.println("Connection closed: " + sessionId + " with status: " + status);
    }

    private void broadcastMessage(String senderId, String message) {
        sessions.forEach((id, session) -> {
            if (!id.equals(senderId) && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage("Broadcast from " + senderId + ": " + message));
                } catch (IOException e) {
                    System.err.println("Error broadcasting message to " + id + ": " + e.getMessage());
                }
            }
        });
    }
} 