package com.example.backend.controller

import com.example.backend.model.HomePlan
import com.example.backend.service.HomePlanService
import com.example.backend.service.ImageService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = ["*"])
class HomePlanController(
    private val homePlanService: HomePlanService,
    private val imageService: ImageService
) {
    @GetMapping
    fun getAllPlans(): List<HomePlan> = homePlanService.getAllPlans()

    @GetMapping("/{id}")
    fun getPlanById(@PathVariable id: Long): ResponseEntity<HomePlan> {
        val plan = homePlanService.getPlanById(id)
        return if (plan != null) {
            ResponseEntity.ok(plan)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createPlan(@RequestBody plan: HomePlan): HomePlan {
        plan.lastUpdated = LocalDateTime.now()
        return homePlanService.savePlan(plan)
    }

    @PostMapping("/with-image")
    fun createPlanWithImage(
        @RequestParam("image") image: MultipartFile,
        @RequestParam("planName") planName: String,
        @RequestParam("internetSpeed") internetSpeed: String,
        @RequestParam("television") television: String,
        @RequestParam("decoder") decoder: String,
        @RequestParam("localPhone") localPhone: String,
        @RequestParam("price") price: Double,
        @RequestParam("tariffCode") tariffCode: String,
        @RequestParam("campaign") campaign: String,
        @RequestParam("description") description: String
    ): HomePlan {
        val imageUrl = imageService.saveImage(image)
        
        val plan = HomePlan(
            planName = planName,
            internetSpeed = internetSpeed,
            television = television,
            decoder = decoder,
            localPhone = localPhone,
            price = price,
            tariffCode = tariffCode,
            campaign = campaign,
            imageUrl = imageUrl,
            description = description
        )
        
        return homePlanService.savePlan(plan)
    }

    @PutMapping("/{id}")
    fun updatePlan(@PathVariable id: Long, @RequestBody plan: HomePlan): ResponseEntity<HomePlan> {
        val existingPlan = homePlanService.getPlanById(id)
        return if (existingPlan != null) {
            plan.lastUpdated = LocalDateTime.now()
            ResponseEntity.ok(homePlanService.savePlan(plan))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletePlan(@PathVariable id: Long): ResponseEntity<Unit> {
        val plan = homePlanService.getPlanById(id)
        return if (plan != null) {
            // Eliminar la imagen si existe
            val fileName = plan.imageUrl.substringAfterLast('/')
            imageService.deleteImage(fileName)
            
            homePlanService.deletePlan(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchPlans(
        @RequestParam query: String?,
        @RequestParam minPrice: Double?,
        @RequestParam maxPrice: Double?,
        @RequestParam campaign: String?
    ): List<HomePlan> {
        return homePlanService.searchPlans(query, minPrice, maxPrice, campaign)
    }
} 