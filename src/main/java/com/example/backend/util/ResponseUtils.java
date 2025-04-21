package com.example.backend.util;

import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    public static ResponseEntity<Object> success(Object data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity<Object> error(String message) {
        return ResponseEntity.badRequest().body(message);
    }
}