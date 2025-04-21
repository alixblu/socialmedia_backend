package com.example.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}