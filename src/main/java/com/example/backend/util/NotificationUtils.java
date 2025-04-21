package com.example.backend.util;

public class NotificationUtils {

    public static String generateFriendRequestMessage(String senderUsername) {
        return senderUsername + " sent you a friend request.";
    }

    public static String generateAlertMessage(String details) {
        return "Alert: " + details;
    }
}
