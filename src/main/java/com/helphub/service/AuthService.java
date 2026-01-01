package com.helphub.service;

import com.helphub.db.Db;
import com.helphub.model.User;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Authentication service
 */
@Service
public class AuthService {
    private final Db db;
    private final JwtService jwtService;

    public AuthService(Db db, JwtService jwtService) {
        this.db = db;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user
     */
    public AuthResponse register(String username, String fullName, String phone) {
        try {
            // Check if username already exists
            User existing = db.findUserByUsername(username);
            if (existing != null) {
                return new AuthResponse(false, "Username already exists", null, null);
            }

            // Create user
            User user = db.createUser(username, fullName, phone);
            String token = jwtService.generateToken(username);

            return new AuthResponse(true, "Registration successful", token, user);
        } catch (SQLException e) {
            return new AuthResponse(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }

    /**
     * Login existing user
     */
    public AuthResponse login(String username) {
        try {
            User user = db.findUserByUsername(username);
            if (user == null) {
                return new AuthResponse(false, "User not found", null, null);
            }

            String token = jwtService.generateToken(username);
            return new AuthResponse(true, "Login successful", token, user);
        } catch (SQLException e) {
            return new AuthResponse(false, "Login failed: " + e.getMessage(), null, null);
        }
    }

    /**
     * Auth response DTO
     */
    public static class AuthResponse {
        private boolean success;
        private String message;
        private String token;
        private User user;

        public AuthResponse(boolean success, String message, String token, User user) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }
}



