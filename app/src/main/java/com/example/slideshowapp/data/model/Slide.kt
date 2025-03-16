package com.example.slideshowapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slides")
data class Slide(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val documentUrl: String?,
    val category: SlideCategory,
    val order: Int
)

enum class SlideCategory {
    HOME_PRODUCTS,
    POSTPAID_PRODUCTS
} 