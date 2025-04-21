package com.example.backend.util;

public class StringUtils {

    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("[^A-Za-z0-9_]", "");
    }

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
}