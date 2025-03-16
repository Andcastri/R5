package com.example.slideshowapp.data.repository

import com.example.slideshowapp.data.dao.SlideDao
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.SlideCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SlideRepository @Inject constructor(
    private val slideDao: SlideDao
) {
    fun getSlidesByCategory(category: SlideCategory): Flow<List<Slide>> {
        return slideDao.getSlidesByCategory(category)
    }

    suspend fun createSlide(slide: Slide) {
        val maxOrder = slideDao.getMaxOrderForCategory(slide.category) ?: -1
        val slideWithOrder = slide.copy(order = maxOrder + 1)
        slideDao.insertSlide(slideWithOrder)
    }

    suspend fun updateSlide(slide: Slide) {
        slideDao.updateSlide(slide)
    }

    suspend fun deleteSlide(slide: Slide) {
        slideDao.deleteSlide(slide)
    }
} 