package com.example.backend.util;

import com.example.backend.model.NotificationType;

public class EnumUtils {

    public static NotificationType getNotificationType(String type) {
        try {
            return NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Invalid type
        }
    }
}