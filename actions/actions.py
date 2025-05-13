# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions


# This is a simple example for a custom action which utters "Hello World!"

from typing import Any, Text, Dict, List

from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests
import json

class ActionAskOllama(Action):
    def name(self) -> Text:
        return "action_ask_ollama"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        # Get user message
        user_message = tracker.latest_message.get('text')
        
        # Get the intent with highest confidence
        latest_intent = tracker.latest_message.get('intent').get('name')
        
        # Create a prompt that includes intent context
        prompt = f"You are a helpful assistant. The user said: '{user_message}'. Their intent was detected as '{latest_intent}'. Respond appropriately."
        
        try:
            # Call Ollama API (running locally)
            response = requests.post('http://localhost:11434/api/generate', 
                                    json={
                                        'model': 'phi', 
                                        'prompt': prompt,
                                        'stream': False
                                    }, timeout=30)
            
            # Parse the response
            if response.status_code == 200:
                bot_response = response.json().get('response', "I'm not sure how to respond to that.")
                dispatcher.utter_message(text=bot_response)
            else:
                dispatcher.utter_message(text="I'm having trouble connecting to my brain right now.")
                
        except Exception as e:
            print(f"Error calling Ollama API: {str(e)}")
            dispatcher.utter_message(text="Sorry, I'm having technical difficulties at the moment.")
        
        return []

class ActionOllamaFallback(Action):
    def name(self) -> Text:
        return "action_ollama_fallback"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        # Get the latest user message
        latest_message = tracker.latest_message.get('text')
        
        try:
            # Call Ollama API
            response = requests.post(
                "http://localhost:11434/api/generate",
                json={
                    "model": "phi",
                    "prompt": latest_message,
                    "stream": False
                }
            )
            
            if response.status_code == 200:
                result = response.json()
                # Extract the response from Ollama
                ollama_response = result.get('response', 'I apologize, but I could not generate a response.')
                
                # Send the response back to the user
                dispatcher.utter_message(text=ollama_response)
            else:
                dispatcher.utter_message(text="I apologize, but I'm having trouble connecting to my language model. Please try again later.")
                
        except Exception as e:
            dispatcher.utter_message(text="I apologize, but I encountered an error. Please try again later.")
        
        return []

# You can add more custom actions here if needed
