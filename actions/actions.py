from typing import Any, Text, Dict, List
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests
import json
from datetime import datetime
from requests.exceptions import RequestException

# Common request timeout in seconds
REQUEST_TIMEOUT = 5

class ActionHandlePostCount(Action):
    def name(self) -> Text:
        return "action_handle_post_count"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            # Get the user ID from the tracker
            user_id = tracker.sender_id
            
            # Call the Spring Boot endpoint
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/post-count",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                count = response.json()
                dispatcher.utter_message(text=f"Rasa: You have {count} posts in total.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your post count at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleFriendCount(Action):
    def name(self) -> Text:
        return "action_handle_friend_count"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/friend-count",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                count = response.json()
                dispatcher.utter_message(text=f"Rasa: You have {count} friends in your network.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your friend count at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleLikesReceived(Action):
    def name(self) -> Text:
        return "action_handle_likes_received"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/likes-received",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                count = response.json()
                dispatcher.utter_message(text=f"Rasa: You have received {count} likes in total.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your likes count at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleCommentsWritten(Action):
    def name(self) -> Text:
        return "action_handle_comments_written"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/comments-written",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                count = response.json()
                dispatcher.utter_message(text=f"Rasa: You have written {count} comments in total.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your comments count at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleLastPost(Action):
    def name(self) -> Text:
        return "action_handle_last_post"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/last-post",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                post = response.json()
                created_at = datetime.fromisoformat(post['createdAt'].replace('Z', '+00:00'))
                dispatcher.utter_message(text=f"Rasa: Your last post was on {created_at.strftime('%B %d, %Y')} with the content: '{post['content']}'")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your last post at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleMostLikedPost(Action):
    def name(self) -> Text:
        return "action_handle_most_liked_post"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/most-liked-post",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                post = response.json()
                dispatcher.utter_message(text=f"Rasa: Your most liked post has {post['likeCount']} likes. The content was: '{post['content']}'")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your most liked post at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleRecentFriends(Action):
    def name(self) -> Text:
        return "action_handle_recent_friends"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/recent-friends",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                friends = response.json()
                if friends:
                    friend_names = [friend['friend']['username'] for friend in friends[:5]]
                    dispatcher.utter_message(text=f"Rasa: Your most recent friends are: {', '.join(friend_names)}")
                else:
                    dispatcher.utter_message(text="Rasa: You haven't added any friends recently.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your recent friends at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleLastWeekPosts(Action):
    def name(self) -> Text:
        return "action_handle_last_week_posts"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/last-week-posts",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                posts = response.json()
                if posts:
                    dispatcher.utter_message(text=f"Rasa: You made {len(posts)} posts in the last week.")
                else:
                    dispatcher.utter_message(text="Rasa: You haven't made any posts in the last week.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your last week's posts at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleTopPosts(Action):
    def name(self) -> Text:
        return "action_handle_top_posts"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/top-posts",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                posts = response.json()
                if posts:
                    dispatcher.utter_message(text=f"Rasa: Your top {len(posts)} posts have received the most engagement.")
                else:
                    dispatcher.utter_message(text="Rasa: You don't have any posts yet.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your top posts at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleMonthlyPosts(Action):
    def name(self) -> Text:
        return "action_handle_monthly_posts"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/monthly-posts",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                posts = response.json()
                if posts:
                    dispatcher.utter_message(text=f"Rasa: You have made {len(posts)} posts this month.")
                else:
                    dispatcher.utter_message(text="Rasa: You haven't made any posts this month.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your monthly posts at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleMostCommentedPost(Action):
    def name(self) -> Text:
        return "action_handle_most_commented_post"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/most-commented-post",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                post = response.json()
                dispatcher.utter_message(text=f"Rasa: Your most commented post has {post['commentCount']} comments. The content was: '{post['content']}'")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your most commented post at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleProfileInfo(Action):
    def name(self) -> Text:
        return "action_handle_profile_info"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/profile",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                profile = response.json()
                join_date = datetime.fromisoformat(profile['joinDate'].replace('Z', '+00:00'))
                dispatcher.utter_message(text=f"Rasa: Your profile bio is: '{profile['bio']}'. You joined on {join_date.strftime('%B %d, %Y')}.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your profile information at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return []

class ActionHandleMostActiveMonth(Action):
    def name(self) -> Text:
        return "action_handle_most_active_month"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
        try:
            user_id = tracker.sender_id
            response = requests.get(
                f"http://localhost:8080/api/stats/user/{user_id}/most-active-month",
                timeout=REQUEST_TIMEOUT
            )
            
            if response.status_code == 200:
                data = response.json()
                month_name = datetime(2000, data['month'], 1).strftime('%B')
                dispatcher.utter_message(text=f"Rasa: Your most active month was {month_name} with {data['count']} posts.")
            else:
                dispatcher.utter_message(text="Rasa: I couldn't retrieve your most active month at the moment.")
        except RequestException:
            dispatcher.utter_message(text="Rasa: I'm having trouble connecting to the server. Please try again later.")
        
        return [] 