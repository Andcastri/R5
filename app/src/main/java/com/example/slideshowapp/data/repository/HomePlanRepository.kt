package com.example.slideshowapp.data.repository

import com.example.slideshowapp.data.api.HomePlanApi
import com.example.slideshowapp.data.dao.HomePlanDao
import com.example.slideshowapp.data.model.HomePlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePlanRepository @Inject constructor(
    private val api: HomePlanApi,
    private val dao: HomePlanDao
) {
    // Obtener todos los planes
    fun getAllPlans(): Flow<List<HomePlan>> = flow {
        // Emitir datos locales primero
        emitAll(dao.getAllPlans())
        
        try {
            // Obtener datos del servidor
            val remotePlans = api.getAllPlans()
            // Actualizar base de datos local
            remotePlans.forEach { plan ->
                dao.insertPlan(plan)
            }
        } catch (e: Exception) {
            // Manejar error de red
            e.printStackTrace()
        }
    }

    // Buscar planes
    fun searchPlans(query: String?, minPrice: Double?, maxPrice: Double?, campaign: String?): Flow<List<HomePlan>> = flow {
        try {
            val remotePlans = api.searchPlans(query, minPrice, maxPrice, campaign)
            emit(remotePlans)
        } catch (e: Exception) {
            // Si falla la búsqueda remota, usar búsqueda local
            emitAll(dao.searchPlans(query ?: ""))
        }
    }

    // Crear plan con imagen
    suspend fun createPlanWithImage(
        plan: HomePlan,
        imageFile: File
    ): HomePlan = withContext(Dispatchers.IO) {
        val image = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
        )

        val planName = plan.planName.toRequestBody("text/plain".toMediaTypeOrNull())
        val internetSpeed = plan.internetSpeed.toRequestBody("text/plain".toMediaTypeOrNull())
        val television = plan.television.toRequestBody("text/plain".toMediaTypeOrNull())
        val decoder = plan.decoder.toRequestBody("text/plain".toMediaTypeOrNull())
        val localPhone = plan.localPhone.toRequestBody("text/plain".toMediaTypeOrNull())
        val price = plan.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tariffCode = plan.tariffCode.toRequestBody("text/plain".toMediaTypeOrNull())
        val campaign = plan.campaign.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = plan.description.toRequestBody("text/plain".toMediaTypeOrNull())

        val remotePlan = api.createPlanWithImage(
            image, planName, internetSpeed, television, decoder,
            localPhone, price, tariffCode, campaign, description
        )

        // Guardar en la base de datos local
        dao.insertPlan(remotePlan)
        remotePlan
    }

    // Actualizar plan
    suspend fun updatePlan(plan: HomePlan) = withContext(Dispatchers.IO) {
        try {
            val response = api.updatePlan(plan.id, plan)
            if (response.isSuccessful) {
                dao.updatePlan(plan)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Actualizar localmente si falla la conexión
            dao.updatePlan(plan)
        }
    }

    // Eliminar plan
    suspend fun deletePlan(plan: HomePlan) = withContext(Dispatchers.IO) {
        try {
            val response = api.deletePlan(plan.id)
            if (response.isSuccessful) {
                dao.deletePlan(plan)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Eliminar localmente si falla la conexión
            dao.deletePlan(plan)
        }
    }

    suspend fun getPlanById(id: Long): HomePlan? = dao.getPlanById(id)

    fun filterByPriceRange(minPrice: Double, maxPrice: Double): Flow<List<HomePlan>> = dao.filterByPriceRange(minPrice, maxPrice)

    fun getPlansByCampaign(campaign: String): Flow<List<HomePlan>> = dao.getPlansByCampaign(campaign)
} 