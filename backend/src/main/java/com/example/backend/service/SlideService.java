package com.example.backend.service;

import com.example.backend.model.Slide;
import com.example.backend.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SlideService {
    
    @Autowired
    private SlideRepository slideRepository;
    
    public List<Slide> getAllSlides() {
        return slideRepository.findAllByOrderByOrderIndexAsc();
    }
    
    public Optional<Slide> getSlideById(Long id) {
        return slideRepository.findById(id);
    }
    
    @Transactional
    public Slide createSlide(Slide slide) {
        return slideRepository.save(slide);
    }
    
    @Transactional
    public Optional<Slide> updateSlide(Long id, Slide slideDetails) {
        return slideRepository.findById(id)
            .map(slide -> {
                slide.setTitle(slideDetails.getTitle());
                slide.setDescription(slideDetails.getDescription());
                slide.setImageUrl(slideDetails.getImageUrl());
                slide.setOrderIndex(slideDetails.getOrderIndex());
                return slideRepository.save(slide);
            });
    }
    
    @Transactional
    public boolean deleteSlide(Long id) {
        return slideRepository.findById(id)
            .map(slide -> {
                slideRepository.delete(slide);
                return true;
            })
            .orElse(false);
    }
} 