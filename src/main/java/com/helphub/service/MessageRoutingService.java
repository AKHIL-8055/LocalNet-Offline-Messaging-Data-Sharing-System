package com.helphub.service;

import com.helphub.db.Db;
import com.helphub.model.Message;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for routing messages (broadcast, DM, SOS)
 */
@Service
public class MessageRoutingService {
    private final Db db;

    public MessageRoutingService(Db db) {
        this.db = db;
    }

    /**
     * Save and route message
     */
    public Message saveMessage(Message message) throws SQLException {
        return db.saveMessage(message);
    }

    /**
     * Get message history for a user
     */
    public List<Message> getHistory(String username, int limit) throws SQLException {
        return db.getMessageHistory(username, limit);
    }
}



