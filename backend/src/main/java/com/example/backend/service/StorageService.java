package com.example.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl);
    String uploadFile(MultipartFile file) throws IOException;
    Resource loadFileAsResource(String fileName) throws IOException;
    void deleteFile(String fileName) throws IOException;
    boolean fileExists(String fileName);
} 