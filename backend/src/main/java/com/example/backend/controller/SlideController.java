package com.example.backend.controller;

import com.example.backend.model.Slide;
import com.example.backend.service.SlideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public Slide createSlide(@RequestBody Slide slide) {
        return slideService.createSlide(slide);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Slide> updateSlide(@PathVariable Long id, @RequestBody Slide slideDetails) {
        return slideService.updateSlide(id, slideDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlide(@PathVariable Long id) {
        return slideService.deleteSlide(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
} 