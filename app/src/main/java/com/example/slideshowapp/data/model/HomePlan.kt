package com.example.slideshowapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "home_plans")
data class HomePlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planName: String,
    val internetSpeed: String,
    val television: String,
    val decoder: String,
    val localPhone: String,
    val price: Double,
    val tariffCode: String,
    val campaign: String,
    val imageUrl: String,
    val description: String,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
) 