package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthTestController {

    @GetMapping("/auth/test")
    public ResponseEntity<?> testAuth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null) {
            response.put("status", "NOT_AUTHENTICATED");
            response.put("message", "Authentication is null");
        } else if (!authentication.isAuthenticated()) {
            response.put("status", "NOT_AUTHENTICATED");
            response.put("message", "Not authenticated");
            response.put("authentication", authentication.toString());
        } else {
            response.put("status", "AUTHENTICATED");
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
        }

        return ResponseEntity.ok(response);
    }
}