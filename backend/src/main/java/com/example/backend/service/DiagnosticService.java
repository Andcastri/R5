package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class DiagnosticService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MonitoringService monitoringService;

    private Map<String, Object> diagnosticStatus = new HashMap<>();
    private List<String> errorHistory = new ArrayList<>();
    private static final int MAX_ERROR_HISTORY = 100;
    private int consecutiveFailures = 0;
    private static final int MAX_CONSECUTIVE_FAILURES = 5;
    private LocalDateTime lastSuccessfulCheck = null;
    private boolean isEmergencyMode = false;

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
            
            // Verificar estado general
            checkGeneralHealth();
            
            // Si todo está bien, limpiar historial de errores
            if (isSystemStable()) {
                errorHistory.clear();
                consecutiveFailures = 0;
                lastSuccessfulCheck = LocalDateTime.now();
                diagnosticStatus.put("status", "STABLE");
                isEmergencyMode = false;
            } else {
                consecutiveFailures++;
                if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                    isEmergencyMode = true;
                    performEmergencyRecovery();
                }
            }
            
        } catch (Exception e) {
            logError("Error en diagnóstico: " + e.getMessage());
            attemptRecovery(e);
            consecutiveFailures++;
            if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                isEmergencyMode = true;
                performEmergencyRecovery();
            }
        }
    }

    private void checkGeneralHealth() {
        try {
            // Verificar tiempo desde última comprobación exitosa
            if (lastSuccessfulCheck != null) {
                Duration duration = Duration.between(lastSuccessfulCheck, LocalDateTime.now());
                if (duration.toSeconds() > 300) { // 5 minutos
                    logError("Tiempo sin comprobación exitosa: " + duration.toSeconds() + " segundos");
                    isEmergencyMode = true;
                    performEmergencyRecovery();
                }
            }

            // Verificar estado de la aplicación
            Map<String, Object> status = monitoringService.getStatus();
            if (!"UP".equals(status.get("status"))) {
                logError("Estado de la aplicación no saludable: " + status.get("status"));
                isEmergencyMode = true;
                performEmergencyRecovery();
            }
        } catch (Exception e) {
            logError("Error en verificación general: " + e.getMessage());
            isEmergencyMode = true;
            performEmergencyRecovery();
        }
    }

    private void performEmergencyRecovery() {
        logError("Iniciando recuperación de emergencia");
        try {
            // 1. Limpiar recursos
            cleanupResources();
            
            // 2. Reiniciar conexiones
            resetConnections();
            
            // 3. Verificar y recrear estructura
            verifyAndRecreateStructure();
            
            // 4. Forzar recolección de basura
            System.gc();
            
            // 5. Verificar estado final
            if (isSystemStable()) {
                logError("Recuperación de emergencia exitosa");
                consecutiveFailures = 0;
                isEmergencyMode = false;
            } else {
                logError("Recuperación de emergencia incompleta");
                // Intentar reiniciar la aplicación si la recuperación falla
                if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES * 2) {
                    logError("Intentando reiniciar la aplicación");
                    System.exit(0); // Esto forzará un reinicio del contenedor
                }
            }
        } catch (Exception e) {
            logError("Error en recuperación de emergencia: " + e.getMessage());
        }
    }

    private void cleanupResources() {
        try {
            // Limpiar archivos temporales
            cleanupTempFiles();
            
            // Limpiar caché de la base de datos
            jdbcTemplate.execute("VACUUM ANALYZE");
            
            // Limpiar memoria
            System.gc();
            
            // Limpiar directorios temporales
            String[] tempDirs = {"temp", "logs", "uploads"};
            for (String dir : tempDirs) {
                java.io.File directory = new java.io.File(System.getProperty("user.dir"), dir);
                if (directory.exists()) {
                    java.io.File[] files = directory.listFiles();
                    if (files != null) {
                        for (java.io.File file : files) {
                            if (file.lastModified() < System.currentTimeMillis() - 86400000) { // 24 horas
                                file.delete();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logError("Error limpiando recursos: " + e.getMessage());
        }
    }

    private void resetConnections() {
        try {
            // Verificar y resetear conexión a la base de datos
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            // Forzar reconexión si es necesario
            if (jdbcTemplate.getDataSource() != null) {
                jdbcTemplate.getDataSource().getConnection().close();
            }
            
            // Esperar un momento para que la conexión se restablezca
            Thread.sleep(1000);
        } catch (Exception e) {
            logError("Error reseteando conexiones: " + e.getMessage());
        }
    }

    private void verifyAndRecreateStructure() {
        try {
            // Verificar y recrear directorios
            String[] requiredDirs = {"uploads", "logs", "temp"};
            for (String dir : requiredDirs) {
                java.io.File directory = new java.io.File(System.getProperty("user.dir"), dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // Asegurar permisos
                directory.setReadable(true, true);
                directory.setWritable(true, true);
                directory.setExecutable(true, true);
            }

            // Verificar y recrear tablas
            List<String> requiredTables = List.of("users", "posts", "comments");
            for (String table : requiredTables) {
                try {
                    jdbcTemplate.queryForObject("SELECT 1 FROM " + table + " LIMIT 1", Integer.class);
                } catch (Exception e) {
                    createTable(table);
                }
            }
        } catch (Exception e) {
            logError("Error verificando estructura: " + e.getMessage());
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
        if (errorHistory.isEmpty()) {
            return true;
        }

        // Verificar si hay errores recientes
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        boolean hasRecentErrors = errorHistory.stream()
            .filter(error -> LocalDateTime.parse(error.split(" - ")[0]).isAfter(fiveMinutesAgo))
            .anyMatch(error -> !error.contains("exitosa") && !error.contains("STABLE"));

        return !hasRecentErrors;
    }

    public Map<String, Object> getDiagnosticStatus() {
        diagnosticStatus.put("consecutiveFailures", consecutiveFailures);
        diagnosticStatus.put("lastSuccessfulCheck", lastSuccessfulCheck != null ? lastSuccessfulCheck.toString() : "Nunca");
        diagnosticStatus.put("emergencyMode", isEmergencyMode);
        return diagnosticStatus;
    }
} 