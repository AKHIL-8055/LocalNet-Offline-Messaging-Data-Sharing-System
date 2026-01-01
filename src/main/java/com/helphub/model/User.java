package com.helphub.model;

import java.time.LocalDateTime;

/**
 * User entity for authentication and chat
 */
public class User {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private LocalDateTime createdAt;
    private boolean online;

    public User() {
    }

    public User(String username, String fullName, String phone) {
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
        this.online = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}



