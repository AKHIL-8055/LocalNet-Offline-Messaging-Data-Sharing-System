package com.helphub.controller;

import com.helphub.db.Db;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for server information
 */
@RestController
@RequestMapping("/api/server")
@CrossOrigin(origins = "*")
public class ServerInfoController {
    private final String serverName;
    private final Db db;

    public ServerInfoController(@Value("${app.server.name}") String serverName, Db db) {
        this.serverName = serverName;
        this.db = db;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("serverName", serverName);
        try {
            info.put("onlineUsers", db.getOnlineUserCount());
        } catch (SQLException e) {
            info.put("onlineUsers", 0);
        }
        return ResponseEntity.ok(info);
    }
}



