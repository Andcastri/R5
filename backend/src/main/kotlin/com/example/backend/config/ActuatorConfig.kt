package com.example.backend.config

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ActuatorConfig {
    
    @Bean
    fun healthIndicator(): HealthIndicator {
        return object : HealthIndicator {
            override fun health(): Health {
                return Health.up().build()
            }
        }
    }
} 