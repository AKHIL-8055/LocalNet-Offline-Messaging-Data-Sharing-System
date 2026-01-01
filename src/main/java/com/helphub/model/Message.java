package com.helphub.model;

import java.time.LocalDateTime;

/**
 * Message entity for chat messages
 */
public class Message {
    private Long id;
    private String sender;
    private String senderFullName;
    private String content;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDateTime timestamp;
    private boolean isSOS;
    private String recipient; // null for broadcast, username for DM

    public Message() {
        this.timestamp = LocalDateTime.now();
        this.isSOS = false;
    }

    public Message(String sender, String senderFullName, String content) {
        this.sender = sender;
        this.senderFullName = senderFullName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isSOS = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSOS() {
        return isSOS;
    }

    public void setSOS(boolean SOS) {
        isSOS = SOS;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}



