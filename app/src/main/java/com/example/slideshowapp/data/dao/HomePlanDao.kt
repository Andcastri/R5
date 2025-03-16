package com.example.slideshowapp.data.dao

import androidx.room.*
import com.example.slideshowapp.data.model.HomePlan
import kotlinx.coroutines.flow.Flow

@Dao
interface HomePlanDao {
    @Query("SELECT * FROM home_plans ORDER BY planName ASC")
    fun getAllPlans(): Flow<List<HomePlan>>

    @Query("SELECT * FROM home_plans WHERE id = :planId")
    suspend fun getPlanById(planId: Long): HomePlan?

    @Query("SELECT * FROM home_plans WHERE planName LIKE '%' || :searchQuery || '%' OR tariffCode LIKE '%' || :searchQuery || '%'")
    fun searchPlans(searchQuery: String): Flow<List<HomePlan>>

    @Query("SELECT * FROM home_plans WHERE price BETWEEN :minPrice AND :maxPrice")
    fun filterByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<HomePlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: HomePlan): Long

    @Update
    suspend fun updatePlan(plan: HomePlan)

    @Delete
    suspend fun deletePlan(plan: HomePlan)

    @Query("SELECT * FROM home_plans WHERE campaign = :campaignName")
    fun getPlansByCampaign(campaignName: String): Flow<List<HomePlan>>
} 