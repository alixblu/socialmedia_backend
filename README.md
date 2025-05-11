# Chat Application Backend

## AI Integrations

This application provides two AI integration options for the chatbot system:

### OpenAI Integration Setup

1. Sign up for an OpenAI account and obtain an API key at https://platform.openai.com/
2. Set the API key as an environment variable:
   ```
   export OPENAI_API_KEY=your_actual_api_key_here
   ```
   Or add it to your .env file:
   ```
   OPENAI_API_KEY=your_actual_api_key_here
   ```
3. The model can be configured in application.properties:
   ```
   openai.model=gpt-3.5-turbo  # Or use gpt-4 if you have access
   ```

### Rasa NLU Integration Setup

1. Install and set up a Rasa NLU server
2. Run the application with the rasa profile:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=rasa
   ```
3. Configure Rasa settings in application-rasa.properties:
   ```
   rasa.url=http://localhost:5005
   rasa.model=default
   rasa.timeout=10000
   ```

## Features

- Real-time chat with WebSocket support
- Direct messaging between users
- Group chat functionality with member management
- AI chat assistance powered by OpenAI API or Rasa NLU
- Multiple specialized AI bots for different topics (Spring Boot, Java, etc.)

## Technology Stack

- Spring Boot
- Spring WebSocket
- Spring Data MongoDB
- Spring Security
- OpenAI API or Rasa NLU integration

## Running the Application

### With OpenAI (Default)
1. Ensure MongoDB is running locally
2. Set the OpenAI API key as environment variable
3. Run the application using Maven:
   ```
   mvn spring-boot:run
   ```

### With Rasa NLU
1. Ensure MongoDB is running locally
2. Ensure Rasa NLU server is running
3. Run the application with the Rasa profile:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=rasa
   ```

## API Documentation

- REST endpoints for user authentication and chat management
- WebSocket endpoints for real-time messaging
- Specialized AI chatbot integration 