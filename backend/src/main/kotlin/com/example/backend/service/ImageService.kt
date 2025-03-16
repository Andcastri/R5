package com.example.backend.service

import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class ImageService(
    private val azureStorageService: AzureStorageService
) {
    fun saveImage(file: MultipartFile): String {
        // Procesar y optimizar la imagen
        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(file.inputStream)
            .size(1920, 1080) // Resolución Full HD
            .outputQuality(0.85) // 85% de calidad
            .outputFormat("jpg")
            .toOutputStream(outputStream)

        // Crear un nuevo MultipartFile con la imagen optimizada
        val optimizedImage = object : MultipartFile {
            override fun getInputStream() = ByteArrayInputStream(outputStream.toByteArray())
            override fun getName() = file.name
            override fun getOriginalFilename() = file.originalFilename
            override fun getContentType() = "image/jpeg"
            override fun isEmpty() = outputStream.size() == 0L
            override fun getSize() = outputStream.size().toLong()
            override fun getBytes() = outputStream.toByteArray()
            override fun transferTo(dest: java.io.File) {
                dest.writeBytes(outputStream.toByteArray())
            }
        }

        // Subir a Azure Storage
        val fileName = azureStorageService.uploadImage(optimizedImage)
        return "/api/images/$fileName"
    }

    fun getImage(fileName: String): ByteArray {
        return azureStorageService.getImage(fileName)
    }

    fun deleteImage(fileName: String) {
        azureStorageService.deleteImage(fileName)
    }
} 