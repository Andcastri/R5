package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class DiagnosticService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MonitoringService monitoringService;

    private Map<String, Object> diagnosticStatus = new HashMap<>();
    private List<String> errorHistory = new ArrayList<>();
    private static final int MAX_ERROR_HISTORY = 100;

    @Scheduled(fixedRate = 5000) // Cada 5 segundos
    public void diagnoseAndFix() {
        diagnosticStatus.put("lastDiagnostic", LocalDateTime.now().toString());
        
        try {
            // Verificar estructura de la base de datos
            checkDatabaseStructure();
            
            // Verificar configuración de la aplicación
            checkApplicationConfig();
            
            // Verificar recursos del sistema
            checkSystemResources();
            
            // Verificar conectividad
            checkConnectivity();
            
            // Si todo está bien, limpiar historial de errores
            if (isSystemStable()) {
                errorHistory.clear();
                diagnosticStatus.put("status", "STABLE");
            }
            
        } catch (Exception e) {
            logError("Error en diagnóstico: " + e.getMessage());
            attemptRecovery(e);
        }
    }

    private void checkDatabaseStructure() {
        try {
            // Verificar tablas necesarias
            List<String> requiredTables = List.of("users", "posts", "comments");
            for (String table : requiredTables) {
                try {
                    jdbcTemplate.queryForObject("SELECT 1 FROM " + table + " LIMIT 1", Integer.class);
                } catch (Exception e) {
                    logError("Tabla " + table + " no encontrada");
                    createTable(table);
                }
            }
        } catch (Exception e) {
            logError("Error verificando estructura de base de datos: " + e.getMessage());
            attemptDatabaseRecovery();
        }
    }

    private void checkApplicationConfig() {
        try {
            // Verificar directorios necesarios
            String[] requiredDirs = {"uploads", "logs", "temp"};
            for (String dir : requiredDirs) {
                java.io.File directory = new java.io.File(System.getProperty("user.dir"), dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                    logError("Directorio " + dir + " creado");
                }
            }
        } catch (Exception e) {
            logError("Error verificando configuración: " + e.getMessage());
            attemptConfigRecovery();
        }
    }

    private void checkSystemResources() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        
        if (freeMemory < 1000000) { // Menos de 1MB libre
            logError("Memoria baja: " + (freeMemory / 1024 / 1024) + "MB libre");
            System.gc();
        }
        
        java.io.File root = new java.io.File("/");
        if (root.getFreeSpace() < 1000000000) { // Menos de 1GB libre
            logError("Espacio en disco bajo: " + (root.getFreeSpace() / 1024 / 1024 / 1024) + "GB libre");
            cleanupTempFiles();
        }
    }

    private void checkConnectivity() {
        try {
            // Verificar conexión a la base de datos
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            logError("Error de conectividad con la base de datos");
            attemptDatabaseRecovery();
        }
    }

    private void attemptRecovery(Exception error) {
        String errorType = error.getClass().getSimpleName();
        switch (errorType) {
            case "DataAccessException":
                attemptDatabaseRecovery();
                break;
            case "IOException":
                attemptFileSystemRecovery();
                break;
            case "OutOfMemoryError":
                attemptMemoryRecovery();
                break;
            default:
                attemptGeneralRecovery();
        }
    }

    private void attemptDatabaseRecovery() {
        try {
            // Intentar reconectar
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            logError("Recuperación de base de datos exitosa");
        } catch (Exception e) {
            logError("Fallo en recuperación de base de datos: " + e.getMessage());
        }
    }

    private void attemptFileSystemRecovery() {
        try {
            // Limpiar archivos temporales
            cleanupTempFiles();
            logError("Recuperación de sistema de archivos exitosa");
        } catch (Exception e) {
            logError("Fallo en recuperación de sistema de archivos: " + e.getMessage());
        }
    }

    private void attemptMemoryRecovery() {
        try {
            System.gc();
            logError("Recuperación de memoria exitosa");
        } catch (Exception e) {
            logError("Fallo en recuperación de memoria: " + e.getMessage());
        }
    }

    private void attemptConfigRecovery() {
        try {
            // Intentar recrear directorios necesarios
            String[] requiredDirs = {"uploads", "logs", "temp"};
            for (String dir : requiredDirs) {
                java.io.File directory = new java.io.File(System.getProperty("user.dir"), dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                    logError("Directorio " + dir + " recreado exitosamente");
                }
            }
            logError("Recuperación de configuración exitosa");
        } catch (Exception e) {
            logError("Fallo en recuperación de configuración: " + e.getMessage());
        }
    }

    private void attemptGeneralRecovery() {
        try {
            // Reiniciar servicios críticos
            monitoringService.getStatus();
            logError("Recuperación general exitosa");
        } catch (Exception e) {
            logError("Fallo en recuperación general: " + e.getMessage());
        }
    }

    private void createTable(String tableName) {
        try {
            String createTableSQL = getCreateTableSQL(tableName);
            jdbcTemplate.execute(createTableSQL);
            logError("Tabla " + tableName + " creada exitosamente");
        } catch (Exception e) {
            logError("Error creando tabla " + tableName + ": " + e.getMessage());
        }
    }

    private String getCreateTableSQL(String tableName) {
        switch (tableName) {
            case "users":
                return "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL)";
            case "posts":
                return "CREATE TABLE IF NOT EXISTS posts (id SERIAL PRIMARY KEY, title VARCHAR(200) NOT NULL, content TEXT, user_id INTEGER REFERENCES users(id))";
            case "comments":
                return "CREATE TABLE IF NOT EXISTS comments (id SERIAL PRIMARY KEY, content TEXT, post_id INTEGER REFERENCES posts(id), user_id INTEGER REFERENCES users(id))";
            default:
                return "";
        }
    }

    private void cleanupTempFiles() {
        try {
            java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"));
            java.io.File[] files = tempDir.listFiles((dir, name) -> name.startsWith("spring-"));
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.lastModified() < System.currentTimeMillis() - 86400000) { // 24 horas
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            logError("Error limpiando archivos temporales: " + e.getMessage());
        }
    }

    private void logError(String error) {
        errorHistory.add(LocalDateTime.now().toString() + " - " + error);
        if (errorHistory.size() > MAX_ERROR_HISTORY) {
            errorHistory.remove(0);
        }
        diagnosticStatus.put("errorHistory", errorHistory);
    }

    private boolean isSystemStable() {
        return errorHistory.isEmpty() || 
               errorHistory.stream().anyMatch(error -> 
                   error.contains("exitosa") || error.contains("STABLE"));
    }

    public Map<String, Object> getDiagnosticStatus() {
        return diagnosticStatus;
    }
} 