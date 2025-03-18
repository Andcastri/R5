package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.service.MonitoringService;
import com.example.backend.service.AutoRecoveryService;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private AutoRecoveryService autoRecoveryService;

    @GetMapping("/")
    public Map<String, Object> health() {
        Map<String, Object> status = monitoringService.getStatus();
        status.put("recovery", autoRecoveryService.getRecoveryStatus());
        return status;
    }
} 