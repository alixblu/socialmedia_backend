package com.example.backend.controller;

import com.example.backend.model.AiChatMessage;
import com.example.backend.service.OllamaService;
import com.example.backend.service.CustomActionService;
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
@RequestMapping("/ai")
public class AiChatController {
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private CustomActionService customActionService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String RASA_API_URL = "http://localhost:5005/webhooks/rest/webhook";
    private static final int TIMEOUT = 30000; // Increased to 30 seconds

    @PostConstruct
    public void init() {
        logger.info("AiChatController initialized with endpoint: /api/ai/rasa/chat");
        // Configure timeout for RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT);
        factory.setReadTimeout(TIMEOUT);
        restTemplate.setRequestFactory(factory);
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

            // First try Rasa
            logger.info("Attempting to connect to Rasa at: {}", RASA_API_URL);
            Map<String, Object> rasaRequest = new HashMap<>();
            rasaRequest.put("sender", userId.toString());
            rasaRequest.put("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> rasaEntity = new HttpEntity<>(rasaRequest, headers);

            try {
                logger.info("Sending request to Rasa: {}", rasaRequest);
                ResponseEntity<Map[]> rasaResponse = restTemplate.postForEntity(
                    RASA_API_URL,
                    rasaEntity,
                    Map[].class
                );
                logger.info("Rasa response status: {}", rasaResponse.getStatusCode());
                logger.info("Rasa response body: {}", rasaResponse.getBody());

                String response;
                if (rasaResponse.getStatusCode() == HttpStatus.OK && rasaResponse.getBody() != null && rasaResponse.getBody().length > 0) {
                    // Use Rasa's response
                    Map<String, Object> rasaMessage = rasaResponse.getBody()[0];
                    String rasaText = (String) rasaMessage.get("text");
                    if (rasaText != null && !rasaText.isEmpty()) {
                        // Check if this is a database query
                        if (rasaText.contains("post count") || rasaText.contains("how many posts")) {
                            response = customActionService.handlePostCount(userId);
                        } else if (rasaText.contains("friend count") || rasaText.contains("how many friends")) {
                            response = customActionService.handleFriendCount(userId);
                        } else if (rasaText.contains("likes received")) {
                            response = customActionService.handleLikesReceived(userId);
                        } else if (rasaText.contains("comments written")) {
                            response = customActionService.handleCommentsWritten(userId);
                        } else if (rasaText.contains("last post")) {
                            response = customActionService.handleLastPost(userId);
                        } else if (rasaText.contains("most liked post")) {
                            response = customActionService.handleMostLikedPost(userId);
                        } else if (rasaText.contains("most active month")) {
                            response = customActionService.handleMostActiveMonth(userId);
                        } else {
                            response = rasaText;
                        }
                        logger.info("Using processed response: {}", response);
                    } else {
                        // Fallback to Ollama
                        logger.info("No valid response from Rasa, falling back to Ollama");
                        response = ollamaService.generateResponse(message);
                    }
                } else {
                    // Fallback to Ollama
                    logger.info("No valid response from Rasa, falling back to Ollama");
                    response = ollamaService.generateResponse(message);
                }

                // Create response object
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("userId", userId);
                responseMap.put("botId", botId);
                responseMap.put("content", response);
                responseMap.put("timestamp", LocalDateTime.now());

                return ResponseEntity.ok(responseMap);
            } catch (RestClientException e) {
                logger.error("Error connecting to Rasa: {}", e.getMessage(), e);
                // Try Ollama directly
                logger.info("Rasa connection failed, attempting to use Ollama directly");
                String response = ollamaService.generateResponse(message);
                
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("userId", userId);
                responseMap.put("botId", botId);
                responseMap.put("content", response);
                responseMap.put("timestamp", LocalDateTime.now());

                return ResponseEntity.ok(responseMap);
            }
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "An unexpected error occurred. Please try again."
            ));
        }
    }
} 