package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.service.MonitoringService;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private MonitoringService monitoringService;

    @GetMapping("/")
    public Map<String, Object> health() {
        return monitoringService.getStatus();
    }
} 