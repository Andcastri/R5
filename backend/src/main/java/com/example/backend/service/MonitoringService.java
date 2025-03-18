package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MonitoringService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Map<String, Object> lastStatus = new HashMap<>();

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void checkHealth() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now().toString());
        
        try {
            // Verificar base de datos
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            status.put("database", "UP");
        } catch (Exception e) {
            status.put("database", "DOWN");
            status.put("databaseError", e.getMessage());
        }

        // Verificar memoria
        Runtime runtime = Runtime.getRuntime();
        status.put("memory", new HashMap<String, Long>() {{
            put("total", runtime.totalMemory());
            put("free", runtime.freeMemory());
            put("used", runtime.totalMemory() - runtime.freeMemory());
        }});

        // Verificar sistema de archivos
        try {
            java.io.File uploadDir = new java.io.File(System.getProperty("user.dir"), "uploads");
            status.put("storage", new HashMap<String, Object>() {{
                put("exists", uploadDir.exists());
                put("writable", uploadDir.canWrite());
                put("path", uploadDir.getAbsolutePath());
            }});
        } catch (Exception e) {
            status.put("storage", "ERROR");
            status.put("storageError", e.getMessage());
        }

        lastStatus = status;
    }

    public Map<String, Object> getStatus() {
        return lastStatus;
    }
} 