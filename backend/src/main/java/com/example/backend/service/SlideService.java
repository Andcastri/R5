package com.example.backend.service;

import com.example.backend.model.Slide;
import com.example.backend.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SlideService {
    
    @Autowired
    private SlideRepository slideRepository;
    
    @Autowired
    private AzureStorageService azureStorageService;
    
    public List<Slide> getAllSlides() {
        return slideRepository.findAllByOrderByOrderIndexAsc();
    }
    
    public Optional<Slide> getSlideById(Long id) {
        return slideRepository.findById(id);
    }
    
    @Transactional
    public Slide createSlide(Slide slide, MultipartFile image) throws IOException {
        String imageUrl = azureStorageService.uploadImage(image);
        slide.setImageUrl(imageUrl);
        return slideRepository.save(slide);
    }
    
    @Transactional
    public Optional<Slide> updateSlide(Long id, Slide slideDetails, MultipartFile image) throws IOException {
        return slideRepository.findById(id)
            .map(slide -> {
                try {
                    // Si hay una nueva imagen, eliminar la anterior y subir la nueva
                    if (image != null && !image.isEmpty()) {
                        azureStorageService.deleteImage(slide.getImageUrl());
                        String newImageUrl = azureStorageService.uploadImage(image);
                        slide.setImageUrl(newImageUrl);
                    }
                    
                    slide.setTitle(slideDetails.getTitle());
                    slide.setDescription(slideDetails.getDescription());
                    slide.setOrderIndex(slideDetails.getOrderIndex());
                    return slideRepository.save(slide);
                } catch (IOException e) {
                    throw new RuntimeException("Error al procesar la imagen", e);
                }
            });
    }
    
    @Transactional
    public boolean deleteSlide(Long id) {
        return slideRepository.findById(id)
            .map(slide -> {
                try {
                    azureStorageService.deleteImage(slide.getImageUrl());
                    slideRepository.delete(slide);
                    return true;
                } catch (Exception e) {
                    throw new RuntimeException("Error al eliminar la imagen", e);
                }
            })
            .orElse(false);
    }
} 