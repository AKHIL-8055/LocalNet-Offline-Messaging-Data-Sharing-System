package com.helphub.controller;

import com.helphub.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        AuthService.AuthResponse response = authService.register(
                request.getUsername(),
                request.getFullName(),
                request.getPhone()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("success", response.isSuccess());
        result.put("message", response.getMessage());
        if (response.isSuccess()) {
            result.put("token", response.getToken());
            result.put("username", response.getUser().getUsername());
            result.put("fullName", response.getUser().getFullName());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        AuthService.AuthResponse response = authService.login(request.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("success", response.isSuccess());
        result.put("message", response.getMessage());
        if (response.isSuccess()) {
            result.put("token", response.getToken());
            result.put("username", response.getUser().getUsername());
            result.put("fullName", response.getUser().getFullName());
        }

        return ResponseEntity.ok(result);
    }

    // Request DTOs
    public static class RegisterRequest {
        private String username;
        private String fullName;
        private String phone;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class LoginRequest {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}



