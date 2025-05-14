package com.example.backend.controller;

import com.example.backend.model.AiChatMessage;
import com.example.backend.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String RASA_API_URL = "http://localhost:5005/webhooks/rest/webhook";
    private static final int TIMEOUT = 5000; // 5 seconds timeout

    @PostConstruct
    public void init() {
        logger.info("AiChatController initialized with endpoint: /api/ai/rasa/chat");
    }

    @PostMapping("/rasa/chat")
    public ResponseEntity<Map<String, Object>> handleChat(@RequestBody Map<String, Object> request) {
        logger.info("Received chat request: {}", request);
        try {
            Integer userId = (Integer) request.get("userId");
            String message = (String) request.get("message");
            String botId = (String) request.get("botId");

            if (userId == null || message == null) {
                logger.error("Missing required fields: userId or message");
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Missing required fields: userId or message"
                ));
            }

            // Configure timeout
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(TIMEOUT);
            factory.setReadTimeout(TIMEOUT);
            restTemplate.setRequestFactory(factory);

            // First try Ollama for general conversation
            logger.info("Attempting to use Ollama for general conversation");
            String response = ollamaService.generateResponse(message);

            // Then try Rasa for specific intents
            try {
                logger.info("Attempting to connect to Rasa for specific intents");
                Map<String, Object> rasaRequest = new HashMap<>();
                rasaRequest.put("sender", userId.toString());
                rasaRequest.put("message", message);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> rasaEntity = new HttpEntity<>(rasaRequest, headers);

                ResponseEntity<Map[]> rasaResponse = restTemplate.postForEntity(
                    RASA_API_URL,
                    rasaEntity,
                    Map[].class
                );
                logger.info("Rasa response status: {}", rasaResponse.getStatusCode());
                logger.info("Rasa response body: {}", rasaResponse.getBody());

                if (rasaResponse.getStatusCode() == HttpStatus.OK && rasaResponse.getBody() != null && rasaResponse.getBody().length > 0) {
                    // Use Rasa's response if it has one
                    Map<String, Object> rasaMessage = rasaResponse.getBody()[0];
                    String rasaText = (String) rasaMessage.get("text");
                    if (rasaText != null && !rasaText.isEmpty()) {
                        logger.info("Using Rasa response: {}", rasaText);
                        response = "Rasa: " + rasaText;
                    }
                }
            } catch (RestClientException e) {
                logger.error("Error connecting to Rasa: {}", e.getMessage(), e);
                // Continue with Ollama response if Rasa fails
            }

            // Create response object
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("userId", userId);
            responseMap.put("botId", botId);
            responseMap.put("content", response);
            responseMap.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "An unexpected error occurred. Please try again."
            ));
        }
    }
} 