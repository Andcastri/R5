package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.upload-dir}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) throws IOException {
        // Crear el directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Guardar el archivo
        Files.copy(file.getInputStream(), filePath);

        // Retornar la URL relativa
        return "/uploads/" + fileName;
    }

    public void deleteImage(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar el archivo", e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Crear el directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Guardar el archivo
        Files.copy(file.getInputStream(), filePath);

        // Retornar la URL relativa
        return "/uploads/" + fileName;
    }

    public Resource loadFileAsResource(String fileName) throws IOException {
        // Implementar la lógica para cargar un archivo como Resource
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void deleteFile(String fileName) throws IOException {
        // Implementar la lógica para eliminar un archivo
        throw new UnsupportedOperationException("Method not implemented");
    }

    public boolean fileExists(String fileName) {
        // Implementar la lógica para verificar si un archivo existe
        throw new UnsupportedOperationException("Method not implemented");
    }
} 