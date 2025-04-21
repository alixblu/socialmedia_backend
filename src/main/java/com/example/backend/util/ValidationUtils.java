package com.example.backend.util;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[A-Za-z0-9_]{3,15}$");
    }
}