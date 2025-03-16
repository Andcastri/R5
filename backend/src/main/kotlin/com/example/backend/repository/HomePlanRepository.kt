package com.example.backend.repository

import com.example.backend.model.HomePlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HomePlanRepository : JpaRepository<HomePlan, Long> {
    fun findByPlanNameContainingIgnoreCaseOrTariffCodeContainingIgnoreCase(
        planName: String,
        tariffCode: String
    ): List<HomePlan>

    fun findByPriceBetween(minPrice: Double, maxPrice: Double): List<HomePlan>
    
    fun findByCampaign(campaign: String): List<HomePlan>
} 