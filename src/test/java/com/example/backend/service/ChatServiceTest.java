package com.example.backend.service;

import com.example.backend.model.mongo.ChatMessage;
import com.example.backend.repository.mongo.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_ShouldSaveAndSendMessage() {
        // Arrange
        ChatMessage message = new ChatMessage();
        message.setSenderId("sender1");
        message.setReceiverId("receiver1");
        message.setContent("Hello!");

        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(message);

        // Act
        ChatMessage result = chatService.sendMessage(message);

        // Assert
        assertNotNull(result);
        verify(chatMessageRepository).save(message);
        verify(messagingTemplate).convertAndSendToUser(
            eq("receiver1"),
            eq("/topic/messages"),
            eq(message)
        );
    }

    @Test
    void getChatHistory_ShouldReturnMessages() {
        // Arrange
        String senderId = "sender1";
        String receiverId = "receiver1";
        List<ChatMessage> expectedMessages = Arrays.asList(
            new ChatMessage(),
            new ChatMessage()
        );

        when(chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId))
            .thenReturn(expectedMessages);

        // Act
        List<ChatMessage> result = chatService.getChatHistory(senderId, receiverId);

        // Assert
        assertEquals(expectedMessages.size(), result.size());
        verify(chatMessageRepository).findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId);
    }

    @Test
    void markMessagesAsRead_ShouldUpdateStatus() {
        // Arrange
        String receiverId = "receiver1";
        List<ChatMessage> unreadMessages = Arrays.asList(
            new ChatMessage(),
            new ChatMessage()
        );

        when(chatMessageRepository.findByReceiverIdAndStatus(
            receiverId, 
            ChatMessage.MessageStatus.DELIVERED
        )).thenReturn(unreadMessages);

        // Act
        chatService.markMessagesAsRead(receiverId);

        // Assert
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        unreadMessages.forEach(message -> 
            assertEquals(ChatMessage.MessageStatus.READ, message.getStatus())
        );
    }
} 