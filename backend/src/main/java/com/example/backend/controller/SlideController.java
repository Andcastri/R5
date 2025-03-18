package com.example.backend.controller;

import com.example.backend.model.Slide;
import com.example.backend.service.SlideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/slides")
@CrossOrigin(origins = "*")
public class SlideController {

    @Autowired
    private SlideService slideService;

    @GetMapping
    public List<Slide> getAllSlides() {
        return slideService.getAllSlides();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Slide> getSlideById(@PathVariable Long id) {
        return slideService.getSlideById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Slide> createSlide(
            @RequestPart("slide") Slide slide,
            @RequestPart("image") MultipartFile image) {
        try {
            Slide createdSlide = slideService.createSlide(slide, image);
            return ResponseEntity.ok(createdSlide);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Slide> updateSlide(
            @PathVariable Long id,
            @RequestPart("slide") Slide slideDetails,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            return slideService.updateSlide(id, slideDetails, image)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlide(@PathVariable Long id) {
        return slideService.deleteSlide(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
} 