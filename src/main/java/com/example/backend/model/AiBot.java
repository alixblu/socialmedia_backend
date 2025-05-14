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
    }
    
    public AiBot(String id, String name, String avatarUrl, String description) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.capabilities = new ArrayList<>();
        this.suggestedPrompts = new ArrayList<>();
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