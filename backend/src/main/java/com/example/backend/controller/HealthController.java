package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.service.MonitoringService;
import com.example.backend.service.AutoRecoveryService;
import com.example.backend.service.DiagnosticService;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private AutoRecoveryService autoRecoveryService;

    @Autowired
    private DiagnosticService diagnosticService;

    @GetMapping("/")
    public Map<String, Object> health() {
        Map<String, Object> status = monitoringService.getStatus();
        status.put("recovery", autoRecoveryService.getRecoveryStatus());
        status.put("diagnostic", diagnosticService.getDiagnosticStatus());
        return status;
    }
} 