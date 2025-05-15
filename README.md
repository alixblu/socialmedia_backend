# Social Media Platform Backend (Facebook-inspired)

## Overview

This project is a full-featured social media platform inspired by Facebook. It supports user authentication, real-time messaging (direct and group chat), notifications, profile management, file/image uploads to S3-compatible cloud storage, and AI-powered chat assistants. The AI chat leverages Rasa NLU for intent handling and the TinyLlama model (via Ollama) for natural language generation. The AI chat is just one feature among many social networking capabilities.

## Features

- User registration, login, and profile management
- Real-time chat with WebSocket support
- Direct messaging and group chat with member management
- AI chat assistance powered by Rasa NLU (local) and TinyLlama model (via Ollama)
- Multiple specialized AI bots for different topics (Spring Boot, Java, etc.)
- File and image uploads to S3-compatible cloud storage
- News feed, notifications, and more (extendable)

## Technology Stack

- Spring Boot
- Spring WebSocket
- Spring Data JPA (MySQL)
- Spring Data MongoDB
- Spring Security
- Rasa NLU (local AI)
- Ollama (local LLM runner for Phi model)
- S3-compatible cloud storage (for file/image uploads)
- (Frontend: React, see frontend/README.md)

## Database Architecture

The project uses a dual database architecture:

### MySQL (Primary Database)
- Stores core application data:
  - User profiles and authentication
  - Posts and comments
  - Likes and reactions
  - Friendships and relationships
  - Notifications
- Uses Spring Data JPA for data access
- Schema defined in `socialmedia.sql`

### MongoDB (Chat Database)
- Stores chat-related data:
  - AI chat messages and history
  - Chat intents and responses
  - Bot configurations and capabilities
- Uses Spring Data MongoDB for data access
- Optimized for chat message storage and retrieval

## Getting Started

### Prerequisites
- Java 21
- Maven
- MySQL 8.0+
- MongoDB (local, default port or custom)
- Node.js & npm (for frontend)
- Python 3.9 (for Rasa)
- Ollama (for running Phi model locally)
- AWS S3

### Database Setup

#### MySQL Setup
1. Create a new database:
   ```sql
   CREATE DATABASE socialmedia;
   ```
2. Import the schema:
   ```
   mysql -u your_username -p socialmedia < socialmedia.sql
   ```
3. Configure MySQL connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/socialmedia
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

#### MongoDB Setup
- Start MongoDB (default or custom port, e.g. 27018):
  ```
  mongod --port 27018 --dbpath "C:\data\db"
  ```
- Configure MongoDB connection in `application.properties`:
  ```properties
  spring.data.mongodb.uri=mongodb://localhost:27018/socialmedia
  ```

## AI Integration (Rasa NLU + TinyLlama Model via Ollama)

### Rasa NLU Integration Setup (Local AI)

#### 1. Install and Set Up Rasa (Inside backend Directory)

- Create a Python 3.9 virtual environment named `rasa-env` inside this repo directory and activate it:
  ```
  py -3.9 -m venv rasa-env
  .\rasa-env\Scripts\Activate.ps1
  ```
- Upgrade pip and install Rasa:
  ```
  python -m pip install --upgrade pip
  pip install rasa requests
  rasa init
  rasa train
  ```
- **Note:** All Rasa commands (run, train, etc.) should be executed from within the backend directory and with the `rasa-env` virtual environment activated.

#### 2. Start Rasa Services
- In three separate terminals (all in the backend directory with `rasa-env` activated):
  - Terminal 1: Start Rasa server (API enabled, CORS open, port 5005)
    ```
    rasa run --enable-api --cors "*" --port 5005
    ```
  - Terminal 2: Start Rasa action server (port 5055)
    ```
    rasa run actions --port 5055
    ```
  - Terminal 3: (Optional) Check Rasa status
    ```
    curl http://localhost:5005/status
    ```

#### 3. Set Up Ollama and TinyLlama Model
- Download and install Ollama from https://ollama.com/
- Pull the TinyLlama model:
  ```
  ollama pull tinyllama
  ```
- Start Ollama (if not already running):
  ```
  ollama serve
  ```
- The backend will connect to Ollama locally to generate AI responses using the TinyLlama model.


## AWS S3 Configuration
- Create .env file, then set these as environment variables:
  ```
  AWS_S3_BUCKET_NAME=your-bucket-name
  AWS_S3_REGION=your-region
  AWS_ACCESS_KEY_ID=your-access-key
  AWS_SECRET_ACCESS_KEY=your-secret-key
  ```
- Make sure your AWS S3 bucket has the appropriate CORS configuration for your frontend domain
- The bucket should have public read access for uploaded files/images

## Running the Application

### With Rasa NLU + TinyLlama Model (Local AI)
1. Ensure MongoDB is running locally
2. Ensure Rasa NLU server and action server are running (see above)
3. Ensure Ollama is running with the TinyLlama model available
4. Run the application with the Rasa profile:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=rasa
   ```

## API Documentation
- REST endpoints for user authentication, chat management, file/image upload, and social features
- WebSocket endpoints for real-time messaging
- Specialized AI chatbot integration

## Notes
- For API testing, use the provided Postman collection invite (see note.txt)
- The project is modular and can be extended with more social features 