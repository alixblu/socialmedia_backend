package com.example.backend.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyOpenAiService {

    private final OpenAiService openAiClient;
    private final String model;

    public MyOpenAiService(@Value("${openai.model:gpt-3.5-turbo}") String model) {
        // Try to get API key from environment variable first
        String apiKey = System.getenv("OPENAI_API_KEY");
        
        // If not found in environment, try system properties (set by DotEnvConfig)
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getProperty("OPENAI_API_KEY");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("OpenAI API key is required. Please set OPENAI_API_KEY environment variable or in .env file");
        }
        this.openAiClient = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.model = model;
    }

    /**
     * Generates a response using OpenAI's Chat API
     * 
     * @param botRole The system role message that defines the bot's personality
     * @param userMessage The message from the user
     * @param conversationHistory Previous messages for context
     * @return The generated response
     */
    public String generateResponse(String botRole, String userMessage, List<com.example.backend.model.mongo.AiChatMessage> conversationHistory) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            
            // Add system message to define the bot's role
            messages.add(new ChatMessage("system", botRole));
            
            // Add conversation history for context (up to last 10 messages)
            int historyLimit = Math.min(conversationHistory.size(), 10);
            for (int i = conversationHistory.size() - historyLimit; i < conversationHistory.size(); i++) {
                com.example.backend.model.mongo.AiChatMessage message = conversationHistory.get(i);
                String role = message.getRole() == com.example.backend.model.mongo.AiChatMessage.MessageRole.USER ? "user" : "assistant";
                messages.add(new ChatMessage(role, message.getContent()));
            }
            
            // Add the current user message
            messages.add(new ChatMessage("user", userMessage));
            
            // Create and send the request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .maxTokens(500)
                    .temperature(0.7)
                    .build();
            
            ChatCompletionResult result = openAiClient.createChatCompletion(request);
            
            if (result.getChoices() == null || result.getChoices().isEmpty()) {
                return "Không thể tạo phản hồi. Vui lòng thử lại sau.";
            }
            
            return result.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi khi kết nối với OpenAI API: " + e.getMessage();
        }
    }
} 