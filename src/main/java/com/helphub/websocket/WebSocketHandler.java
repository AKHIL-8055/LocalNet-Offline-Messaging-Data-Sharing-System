//package com.helphub.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.helphub.db.Db;
//import com.helphub.model.Message;
//import com.helphub.model.User;
//import com.helphub.service.JwtService;
//import com.helphub.service.MessageRoutingService;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * WebSocket handler for real-time chat
// */
//@Component
//public class WebSocketHandler extends TextWebSocketHandler {
//    private final Map<WebSocketSession, String> sessions = new ConcurrentHashMap<>();
//    private final MessageRoutingService messageRoutingService;
//    private final Db db;
//    private final JwtService jwtService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public WebSocketHandler(MessageRoutingService messageRoutingService, Db db, JwtService jwtService) {
//        this.messageRoutingService = messageRoutingService;
//        this.db = db;
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        // Extract token from query parameter
//        String query = session.getUri() != null ? session.getUri().getQuery() : null;
//        String token = null;
//        if (query != null && query.startsWith("token=")) {
//            token = query.substring(6);
//        }
//
//        if (token == null || token.isEmpty()) {
//            session.close(CloseStatus.BAD_DATA.withReason("No token provided"));
//            return;
//        }
//
//        // Validate token and get username
//        if (!jwtService.validateToken(token)) {
//            session.close(CloseStatus.BAD_DATA.withReason("Invalid token"));
//            return;
//        }
//
//        String username = jwtService.getUsernameFromToken(token);
//        sessions.put(session, username);
//
//        // Set user online
//        try {
//            db.setUserOnline(username, true);
//        } catch (SQLException e) {
//            // Log error but continue
//        }
//
//        // Send connection confirmation
//        sendMessage(session, createSystemMessage("Connected to server"));
//
//        // Load and send message history
//        try {
//            List<Message> history = messageRoutingService.getHistory(username, 100);
//            for (Message msg : history) {
//                sendMessage(session, msg);
//            }
//        } catch (SQLException e) {
//            sendMessage(session, createSystemMessage("Failed to load history"));
//        }
//
//        // Broadcast user joined
//        broadcastUserJoined(username);
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String username = sessions.get(session);
//        if (username == null) {
//            return;
//        }
//
//        try {
//            // Parse incoming message
//            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
//            String type = (String) data.get("type");
//
//            if ("message".equals(type)) {
//                handleChatMessage(session, username, data);
//            } else if ("sos".equals(type)) {
//                handleSOSMessage(session, username, data);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            sendMessage(session, createSystemMessage("Error processing message: " + e.getMessage()));
//        }
//    }
//
//    private void handleChatMessage(WebSocketSession session, String username, Map<String, Object> data) throws SQLException, IOException {
//        String content = (String) data.get("content");
//        String fileUrl = (String) data.get("fileUrl");
//        String fileName = (String) data.get("fileName");
//        String fileType = (String) data.get("fileType");
//        String recipient = (String) data.get("recipient"); // For DM
//
//        // Validate: must have content or file
//        if ((content == null || content.trim().isEmpty()) && (fileUrl == null || fileUrl.isEmpty())) {
//            sendMessage(session, createSystemMessage("Message must have content or file"));
//            return;
//        }
//
//        // Get user info
//        User user = db.findUserByUsername(username);
//        if (user == null) {
//            return;
//        }
//
//        // Handle null/empty content for file-only messages
//        String messageContent = (content != null && !content.trim().isEmpty()) ? content.trim() : null;
//        Message msg = new Message(username, user.getFullName(), messageContent != null ? messageContent : "");
//        if (messageContent == null) {
//            msg.setContent(null); // Allow null content for file-only messages
//        }
//        msg.setFileUrl(fileUrl);
//        msg.setFileName(fileName);
//        msg.setFileType(fileType);
//        msg.setRecipient(recipient);
//
//        // Save message
//        messageRoutingService.saveMessage(msg);
//
//        // Broadcast or send DM
//        if (recipient == null || recipient.isEmpty()) {
//            // Broadcast to all
//            broadcastMessage(msg);
//        } else {
//            // Direct message - send to sender and recipient
//            sendMessage(session, msg);
//            for (Map.Entry<WebSocketSession, String> entry : sessions.entrySet()) {
//                if (entry.getValue().equals(recipient)) {
//                    sendMessage(entry.getKey(), msg);
//                    break;
//                }
//            }
//        }
//    }
//
//    private void handleSOSMessage(WebSocketSession session, String username, Map<String, Object> data) throws SQLException, IOException {
//        String content = (String) data.get("content");
//        if (content == null) {
//            content = "SOS - Emergency assistance needed!";
//        }
//
//        User user = db.findUserByUsername(username);
//        if (user == null) {
//            return;
//        }
//
//        Message msg = new Message(username, user.getFullName(), content);
//        msg.setSOS(true);
//
//        // Save message
//        messageRoutingService.saveMessage(msg);
//
//        // Broadcast SOS to all
//        broadcastMessage(msg);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String username = sessions.remove(session);
//        if (username != null) {
//            try {
//                db.setUserOnline(username, false);
//            } catch (SQLException e) {
//                // Log error
//            }
//        }
//    }
//
//    private void broadcastMessage(Message message) throws IOException {
//        String json = objectMapper.writeValueAsString(message);
//        TextMessage textMessage = new TextMessage(json);
//        for (WebSocketSession s : sessions.keySet()) {
//            if (s.isOpen()) {
//                s.sendMessage(textMessage);
//            }
//        }
//    }
//
//    private void broadcastUserJoined(String username) {
//        // Optional: broadcast user joined message
//    }
//
//    private void sendMessage(WebSocketSession session, Message message) throws IOException {
//        try {
//            String json = objectMapper.writeValueAsString(message);
//            session.sendMessage(new TextMessage(json));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Message createSystemMessage(String content) {
//        Message msg = new Message("SYSTEM", "System", content);
//        return msg;
//    }
//}
//




package com.helphub.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helphub.db.Db;
import com.helphub.model.Message;
import com.helphub.model.User;
import com.helphub.service.JwtService;
import com.helphub.service.MessageRoutingService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time chat
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sessions = new ConcurrentHashMap<>();
    private final MessageRoutingService messageRoutingService;
    private final Db db;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public WebSocketHandler(
            MessageRoutingService messageRoutingService,
            Db db,
            JwtService jwtService,
            ObjectMapper objectMapper
    ) {
        this.messageRoutingService = messageRoutingService;
        this.db = db;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        String token = null;

        if (query != null && query.startsWith("token=")) {
            token = query.substring(6);
        }

        if (token == null || token.isEmpty()) {
            session.close(CloseStatus.BAD_DATA.withReason("No token provided"));
            return;
        }

        if (!jwtService.validateToken(token)) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid token"));
            return;
        }

        String username = jwtService.getUsernameFromToken(token);
        sessions.put(session, username);

        try {
            db.setUserOnline(username, true);
        } catch (SQLException ignored) {}

        sendMessage(session, createSystemMessage("Connected to server"));

        try {
            List<Message> history = messageRoutingService.getHistory(username, 100);
            for (Message msg : history) {
                sendMessage(session, msg);
            }
        } catch (SQLException e) {
            sendMessage(session, createSystemMessage("Failed to load history"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String username = sessions.get(session);
        if (username == null) return;

        Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) data.get("type");

        if ("message".equals(type)) {
            handleChatMessage(session, username, data);
        } else if ("sos".equals(type)) {
            handleSOSMessage(session, username, data);
        }
    }

    private void handleChatMessage(WebSocketSession session, String username, Map<String, Object> data)
            throws SQLException, IOException {

        String content = (String) data.get("content");
        String fileUrl = (String) data.get("fileUrl");
        String fileName = (String) data.get("fileName");
        String fileType = (String) data.get("fileType");
        String recipient = (String) data.get("recipient");

        if ((content == null || content.trim().isEmpty()) &&
                (fileUrl == null || fileUrl.isEmpty())) {
            sendMessage(session, createSystemMessage("Message must have content or file"));
            return;
        }

        User user = db.findUserByUsername(username);
        if (user == null) return;

        Message msg = new Message(username, user.getFullName(), content);
        msg.setFileUrl(fileUrl);
        msg.setFileName(fileName);
        msg.setFileType(fileType);
        msg.setRecipient(recipient);

        messageRoutingService.saveMessage(msg);

        if (recipient == null || recipient.isEmpty()) {
            broadcastMessage(msg);
        } else {
            sendMessage(session, msg);
            for (Map.Entry<WebSocketSession, String> entry : sessions.entrySet()) {
                if (recipient.equals(entry.getValue())) {
                    sendMessage(entry.getKey(), msg);
                }
            }
        }
    }

    private void handleSOSMessage(WebSocketSession session, String username, Map<String, Object> data)
            throws SQLException, IOException {

        String content = (String) data.getOrDefault("content",
                "SOS - Emergency assistance needed!");

        User user = db.findUserByUsername(username);
        if (user == null) return;

        Message msg = new Message(username, user.getFullName(), content);
        msg.setSOS(true);

        messageRoutingService.saveMessage(msg);
        broadcastMessage(msg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        String username = sessions.remove(session);
        if (username != null) {
            try {
                db.setUserOnline(username, false);
            } catch (SQLException ignored) {}
        }
    }

    private void broadcastMessage(Message message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(json);

        for (WebSocketSession s : sessions.keySet()) {
            if (s.isOpen()) {
                s.sendMessage(textMessage);
            }
        }
    }

    private void sendMessage(WebSocketSession session, Message message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }

    private Message createSystemMessage(String content) {
        return new Message("SYSTEM", "System", content);
    }
}
