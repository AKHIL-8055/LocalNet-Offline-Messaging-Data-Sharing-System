package com.helphub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * HelpHub - Offline Emergency Chat System
 * Main application entry point
 */
@SpringBootApplication
@EnableConfigurationProperties
public class HelpHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelpHubApplication.class, args);
    }
}



