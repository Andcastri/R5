package com.example.slideshowapp.data.api

import com.example.slideshowapp.data.model.HomePlan
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface HomePlanApi {
    @GET("plans")
    suspend fun getAllPlans(): List<HomePlan>

    @GET("plans/{id}")
    suspend fun getPlanById(@Path("id") id: Long): Response<HomePlan>

    @POST("plans")
    suspend fun createPlan(@Body plan: HomePlan): HomePlan

    @Multipart
    @POST("plans/with-image")
    suspend fun createPlanWithImage(
        @Part image: MultipartBody.Part,
        @Part("planName") planName: RequestBody,
        @Part("internetSpeed") internetSpeed: RequestBody,
        @Part("television") television: RequestBody,
        @Part("decoder") decoder: RequestBody,
        @Part("localPhone") localPhone: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tariffCode") tariffCode: RequestBody,
        @Part("campaign") campaign: RequestBody,
        @Part("description") description: RequestBody
    ): HomePlan

    @PUT("plans/{id}")
    suspend fun updatePlan(@Path("id") id: Long, @Body plan: HomePlan): Response<HomePlan>

    @DELETE("plans/{id}")
    suspend fun deletePlan(@Path("id") id: Long): Response<Unit>

    @GET("plans/search")
    suspend fun searchPlans(
        @Query("query") query: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("campaign") campaign: String?
    ): List<HomePlan>
} 