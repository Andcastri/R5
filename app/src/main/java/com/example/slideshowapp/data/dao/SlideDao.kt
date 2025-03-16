package com.example.slideshowapp.data.dao

import androidx.room.*
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.SlideCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface SlideDao {
    @Query("SELECT * FROM slides WHERE category = :category ORDER BY `order` ASC")
    fun getSlidesByCategory(category: SlideCategory): Flow<List<Slide>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlide(slide: Slide)

    @Update
    suspend fun updateSlide(slide: Slide)

    @Delete
    suspend fun deleteSlide(slide: Slide)

    @Query("SELECT MAX(`order`) FROM slides WHERE category = :category")
    suspend fun getMaxOrderForCategory(category: SlideCategory): Int?
} 