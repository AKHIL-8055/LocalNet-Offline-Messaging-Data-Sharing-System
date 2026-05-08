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
     * Hash password
     */
    private String hashPassword(String password) {
        if (password == null || password.isEmpty()) return null;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register a new user
     */
    public AuthResponse register(String username, String password, String fullName, String phone) {
        try {
            // Check if username already exists
            User existing = db.findUserByUsername(username);
            if (existing != null) {
                return new AuthResponse(false, "Username already exists", null, null);
            }

            // Create user
            String hashedPassword = hashPassword(password);
            User user = db.createUser(username, hashedPassword, fullName, phone);
            String token = jwtService.generateToken(username);

            return new AuthResponse(true, "Registration successful", token, user);
        } catch (SQLException e) {
            return new AuthResponse(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }

    /**
     * Login existing user
     */
    public AuthResponse login(String username, String password) {
        try {
            User user = db.findUserByUsername(username);
            if (user == null) {
                return new AuthResponse(false, "User not found", null, null);
            }

            // Verify password
            String hashedPassword = hashPassword(password);
            if (user.getPassword() != null && !user.getPassword().equals(hashedPassword)) {
                // To support existing users without password, only check if password exists
                return new AuthResponse(false, "Invalid password", null, null);
            } else if (user.getPassword() == null && password != null && !password.isEmpty()) {
                 return new AuthResponse(false, "This account does not have a password. Leave password empty to login.", null, null);
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



