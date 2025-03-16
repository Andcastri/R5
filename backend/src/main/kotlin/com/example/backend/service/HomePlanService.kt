package com.example.backend.service

import com.example.backend.model.HomePlan
import com.example.backend.repository.HomePlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HomePlanService(
    private val repository: HomePlanRepository
) {
    fun getAllPlans(): List<HomePlan> = repository.findAll()

    fun getPlanById(id: Long): HomePlan? = repository.findById(id).orElse(null)

    @Transactional
    fun savePlan(plan: HomePlan): HomePlan = repository.save(plan)

    @Transactional
    fun deletePlan(id: Long) = repository.deleteById(id)

    fun searchPlans(
        query: String?,
        minPrice: Double?,
        maxPrice: Double?,
        campaign: String?
    ): List<HomePlan> {
        return repository.findAll().filter { plan ->
            var matches = true

            if (!query.isNullOrBlank()) {
                matches = matches && (plan.planName.contains(query, ignoreCase = true) ||
                        plan.tariffCode.contains(query, ignoreCase = true))
            }

            if (minPrice != null) {
                matches = matches && plan.price >= minPrice
            }

            if (maxPrice != null) {
                matches = matches && plan.price <= maxPrice
            }

            if (!campaign.isNullOrBlank()) {
                matches = matches && plan.campaign == campaign
            }

            matches
        }
    }
} 