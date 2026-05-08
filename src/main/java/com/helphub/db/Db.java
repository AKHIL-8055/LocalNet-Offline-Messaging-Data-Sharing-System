package com.helphub.db;

import com.helphub.model.Message;
import com.helphub.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database service for SQLite operations
 */
@Component
public class Db {
    private final String dbUrl;

    public Db(@Value("${spring.datasource.url}") String dbUrl) {
        this.dbUrl = dbUrl;
        initializeDatabase();
    }

    /**
     * Initialize database tables
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Users table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT," +
                    "full_name TEXT NOT NULL," +
                    "phone TEXT," +
                    "created_at TEXT NOT NULL," +
                    "online INTEGER DEFAULT 0" +
                    ")"
                );
            }
            
            // Add password column to existing DB if missing
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE users ADD COLUMN password TEXT");
            } catch (SQLException e) {
                // Ignore, column already exists
            }

            // Messages table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sender TEXT NOT NULL," +
                    "sender_full_name TEXT NOT NULL," +
                    "content TEXT," +
                    "file_url TEXT," +
                    "file_name TEXT," +
                    "file_type TEXT," +
                    "timestamp TEXT NOT NULL," +
                    "is_sos INTEGER DEFAULT 0," +
                    "recipient TEXT" +
                    ")"
                );
            }

            // Create indexes
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp)");
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender)");
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_messages_recipient ON messages(recipient)");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Create a new user
     */
    public User createUser(String username, String password, String fullName, String phone) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO users (username, password, full_name, phone, created_at) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, phone);
            stmt.setString(5, LocalDateTime.now().toString());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                User user = new User(username, password, fullName, phone);
                user.setId(rs.getLong(1));
                // created_at is already set in User constructor, no need to read from ResultSet
                // getGeneratedKeys() only returns generated keys, not all columns
                return user;
            }
            throw new SQLException("Failed to create user");
        }
    }

    /**
     * Find user by username
     */
    public User findUserByUsername(String username) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
                user.setOnline(rs.getInt("online") == 1);
                return user;
            }
            return null;
        }
    }

    /**
     * Set user online status
     */
    public void setUserOnline(String username, boolean online) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE users SET online = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, online ? 1 : 0);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Get count of online users
     */
    public int getOnlineUserCount() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) as count FROM users WHERE online = 1";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    /**
     * Save a message
     */
    public Message saveMessage(Message message) throws SQLException {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO messages (sender, sender_full_name, content, file_url, file_name, file_type, timestamp, is_sos, recipient) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getSenderFullName());
            stmt.setString(3, message.getContent());
            stmt.setString(4, message.getFileUrl());
            stmt.setString(5, message.getFileName());
            stmt.setString(6, message.getFileType());
            stmt.setString(7, message.getTimestamp().toString());
            stmt.setInt(8, message.isSOS() ? 1 : 0);
            stmt.setString(9, message.getRecipient());
            stmt.executeUpdate();

            // Get the generated ID using SQLite's last_insert_rowid()
            try (Statement idStmt = conn.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    message.setId(rs.getLong(1));
                    return message;
                }
            }
            throw new SQLException("Failed to save message");
        }
    }

    /**
     * Get message history (broadcast messages and DMs for the user)
     */
    public List<Message> getMessageHistory(String username, int limit) throws SQLException {
        List<Message> messages = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Get broadcast messages (recipient IS NULL) and DMs (recipient = username OR sender = username with recipient NOT NULL)
            String sql = "SELECT * FROM messages WHERE recipient IS NULL OR recipient = ? OR (sender = ? AND recipient IS NOT NULL) ORDER BY timestamp DESC LIMIT ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getLong("id"));
                msg.setSender(rs.getString("sender"));
                msg.setSenderFullName(rs.getString("sender_full_name"));
                msg.setContent(rs.getString("content"));
                msg.setFileUrl(rs.getString("file_url"));
                msg.setFileName(rs.getString("file_name"));
                msg.setFileType(rs.getString("file_type"));
                msg.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                msg.setSOS(rs.getInt("is_sos") == 1);
                msg.setRecipient(rs.getString("recipient"));
                messages.add(msg);
            }
        }
        // Reverse to show oldest first
        List<Message> reversed = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            reversed.add(messages.get(i));
        }
        return reversed;
    }
}



