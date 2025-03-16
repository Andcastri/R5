package com.example.slideshowapp.ui.slides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.SlideCategory
import com.example.slideshowapp.data.repository.SlideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlidesViewModel @Inject constructor(
    private val slideRepository: SlideRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<SlideCategory>(SlideCategory.HOME_PRODUCTS)
    val selectedCategory: StateFlow<SlideCategory> = _selectedCategory

    val slides: StateFlow<List<Slide>> = _selectedCategory
        .flatMapLatest { category ->
            slideRepository.getSlidesByCategory(category)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setCategory(category: SlideCategory) {
        _selectedCategory.value = category
    }

    fun addSlide(slide: Slide) {
        viewModelScope.launch {
            slideRepository.createSlide(slide)
        }
    }

    fun updateSlide(slide: Slide) {
        viewModelScope.launch {
            slideRepository.updateSlide(slide)
        }
    }

    fun deleteSlide(slide: Slide) {
        viewModelScope.launch {
            slideRepository.deleteSlide(slide)
        }
    }
} 