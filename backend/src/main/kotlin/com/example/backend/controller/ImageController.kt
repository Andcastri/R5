package com.example.backend.controller

import com.example.backend.service.ImageService
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.file.Paths

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService
) {
    @GetMapping("/{fileName}")
    fun getImage(@PathVariable fileName: String): ResponseEntity<ByteArray> {
        val imageBytes = imageService.getImage(fileName)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imageBytes)
    }
} 