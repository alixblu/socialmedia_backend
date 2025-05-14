from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests
import json

class ActionHandlePostCount(Action):
    def name(self) -> Text:
        return "action_handle_post_count"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        # Get the user ID from the tracker
        user_id = tracker.sender_id
        
        # Call the Spring Boot endpoint
        response = requests.get(f"http://localhost:8080/api/stats/user/{user_id}/post-count")
        
        if response.status_code == 200:
            count = response.json()
            dispatcher.utter_message(text=f"Rasa: You have {count} posts in total.")
        else:
            dispatcher.utter_message(text="Rasa: I couldn't retrieve your post count at the moment.")
        
        return []

class ActionHandleFriendCount(Action):
    def name(self) -> Text:
        return "action_handle_friend_count"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        # Get the user ID from the tracker
        user_id = tracker.sender_id
        
        # Call the Spring Boot endpoint
        response = requests.get(f"http://localhost:8080/api/stats/user/{user_id}/friend-count")
        
        if response.status_code == 200:
            count = response.json()
            dispatcher.utter_message(text=f"Rasa: You have {count} friends in your network.")
        else:
            dispatcher.utter_message(text="Rasa: I couldn't retrieve your friend count at the moment.")
        
        return [] 