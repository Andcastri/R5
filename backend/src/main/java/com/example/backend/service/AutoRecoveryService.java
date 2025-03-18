package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AutoRecoveryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MonitoringService monitoringService;

    private Map<String, Object> recoveryStatus = new HashMap<>();
    private int recoveryAttempts = 0;
    private static final int MAX_RECOVERY_ATTEMPTS = 5;

    @Scheduled(fixedRate = 10000) // Cada 10 segundos
    public void checkAndRecover() {
        Map<String, Object> status = monitoringService.getStatus();
        recoveryStatus.put("lastCheck", LocalDateTime.now().toString());
        
        try {
            // Verificar base de datos
            if ("DOWN".equals(status.get("database"))) {
                attemptDatabaseRecovery();
            }

            // Verificar almacenamiento
            Map<String, Object> storage = (Map<String, Object>) status.get("storage");
            if (storage != null && !(Boolean) storage.get("writable")) {
                attemptStorageRecovery();
            }

            // Verificar memoria
            Map<String, Long> memory = (Map<String, Long>) status.get("memory");
            if (memory != null && memory.get("free") < 1000000) { // Menos de 1MB libre
                attemptMemoryRecovery();
            }

            // Si todo está bien, resetear contadores
            if (isSystemHealthy(status)) {
                recoveryAttempts = 0;
                recoveryStatus.put("status", "HEALTHY");
            }

        } catch (Exception e) {
            recoveryStatus.put("error", e.getMessage());
            recoveryStatus.put("status", "RECOVERY_FAILED");
        }
    }

    private void attemptDatabaseRecovery() {
        recoveryStatus.put("databaseRecovery", "IN_PROGRESS");
        try {
            // Intentar reconectar a la base de datos
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            recoveryStatus.put("databaseRecovery", "SUCCESS");
        } catch (Exception e) {
            recoveryStatus.put("databaseRecovery", "FAILED");
            recoveryStatus.put("databaseError", e.getMessage());
        }
    }

    private void attemptStorageRecovery() {
        recoveryStatus.put("storageRecovery", "IN_PROGRESS");
        try {
            java.io.File uploadDir = new java.io.File(System.getProperty("user.dir"), "uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            recoveryStatus.put("storageRecovery", "SUCCESS");
        } catch (Exception e) {
            recoveryStatus.put("storageRecovery", "FAILED");
            recoveryStatus.put("storageError", e.getMessage());
        }
    }

    private void attemptMemoryRecovery() {
        recoveryStatus.put("memoryRecovery", "IN_PROGRESS");
        try {
            System.gc(); // Forzar recolección de basura
            recoveryStatus.put("memoryRecovery", "SUCCESS");
        } catch (Exception e) {
            recoveryStatus.put("memoryRecovery", "FAILED");
            recoveryStatus.put("memoryError", e.getMessage());
        }
    }

    private boolean isSystemHealthy(Map<String, Object> status) {
        return "UP".equals(status.get("database")) &&
               ((Map<String, Object>) status.get("storage")).get("writable").equals(true) &&
               ((Map<String, Long>) status.get("memory")).get("free") > 1000000;
    }

    public Map<String, Object> getRecoveryStatus() {
        return recoveryStatus;
    }
} 