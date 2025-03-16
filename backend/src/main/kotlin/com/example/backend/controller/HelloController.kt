package com.example.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    
    @GetMapping("/")
    fun hello() = "¡Hola! La aplicación está funcionando correctamente."
} 