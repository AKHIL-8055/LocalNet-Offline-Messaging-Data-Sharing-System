package com.helphub.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for file uploads
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {
    private final String uploadDir;

    public FileUploadController(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
        // Create upload directory if it doesn't exist
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create upload directory", e);
            }
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(uploadDir, filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Determine file type
            String contentType = file.getContentType();
            String fileType = "other";
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    fileType = "image";
                } else if (contentType.equals("application/pdf")) {
                    fileType = "pdf";
                } else if (contentType.contains("document") || contentType.contains("text")) {
                    fileType = "document";
                }
            }

            // Return file URL
            String fileUrl = "/api/files/" + filename;
            response.put("success", true);
            response.put("fileUrl", fileUrl);
            response.put("fileName", originalFilename);
            response.put("fileType", fileType);
            response.put("fileSize", file.getSize());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

