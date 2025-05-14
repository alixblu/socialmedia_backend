package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

public class AiBot {
    private String id;
    private String name;
    private String avatarUrl;
    private String description;
    private List<String> capabilities;
    private List<String> suggestedPrompts;
    
    // Constructors
    public AiBot() {
        this.capabilities = new ArrayList<>();
        this.suggestedPrompts = new ArrayList<>();
        initializeCapabilities();
        initializeSuggestedPrompts();
    }
    
    public AiBot(String id, String name, String avatarUrl, String description) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.capabilities = new ArrayList<>();
        this.suggestedPrompts = new ArrayList<>();
        initializeCapabilities();
        initializeSuggestedPrompts();
    }
    
    private void initializeCapabilities() {
        // Post-related capabilities
        capabilities.add("Count user's total posts");
        capabilities.add("Find user's last post");
        capabilities.add("Find user's posts from last week");
        capabilities.add("Find user's top 3 posts");
        capabilities.add("Find user's posts from current month");
        capabilities.add("Find user's most active posting month");
        
        // Like-related capabilities
        capabilities.add("Count total likes received");
        capabilities.add("Find most liked post");
        
        // Comment-related capabilities
        capabilities.add("Count total comments written");
        capabilities.add("Find post with most comments");
        
        // Friendship-related capabilities
        capabilities.add("Count total friends");
        capabilities.add("Find recent friends");
        
        // Profile-related capabilities
        capabilities.add("Get user's bio");
        capabilities.add("Get user's join date");
    }
    
    private void initializeSuggestedPrompts() {
        // Post-related prompts
        suggestedPrompts.add("How many posts have I made?");
        suggestedPrompts.add("When was my last post?");
        suggestedPrompts.add("What did I post last week?");
        suggestedPrompts.add("What are my top 3 posts?");
        suggestedPrompts.add("Can you list all my posts this month?");
        suggestedPrompts.add("What's my most active posting month?");
        
        // Like-related prompts
        suggestedPrompts.add("How many likes have I received?");
        suggestedPrompts.add("What's my most liked post?");
        
        // Comment-related prompts
        suggestedPrompts.add("How many comments have I written?");
        suggestedPrompts.add("Which post had the most comments?");
        
        // Friendship-related prompts
        suggestedPrompts.add("How many friends do I have?");
        suggestedPrompts.add("Who friends me recently?");
        
        // Profile-related prompts
        suggestedPrompts.add("What's my bio?");
        suggestedPrompts.add("When did I join?");
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }
    
    public void addCapability(String capability) {
        this.capabilities.add(capability);
    }
    
    public List<String> getSuggestedPrompts() {
        return suggestedPrompts;
    }
    
    public void setSuggestedPrompts(List<String> suggestedPrompts) {
        this.suggestedPrompts = suggestedPrompts;
    }
    
    public void addSuggestedPrompt(String prompt) {
        this.suggestedPrompts.add(prompt);
    }
} 