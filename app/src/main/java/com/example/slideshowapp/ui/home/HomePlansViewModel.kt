package com.example.slideshowapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.slideshowapp.data.model.HomePlan
import com.example.slideshowapp.data.repository.HomePlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomePlansViewModel @Inject constructor(
    private val repository: HomePlanRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _priceRange = MutableStateFlow(0.0 to Double.MAX_VALUE)
    private val _selectedCampaign = MutableStateFlow<String?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val plans: StateFlow<List<HomePlan>> = combine(
        repository.getAllPlans(),
        _searchQuery,
        _priceRange,
        _selectedCampaign
    ) { plans, query, (minPrice, maxPrice), campaign ->
        plans.filter { plan ->
            val matchesQuery = plan.planName.contains(query, ignoreCase = true) ||
                             plan.tariffCode.contains(query, ignoreCase = true)
            val matchesPrice = plan.price in minPrice..maxPrice
            val matchesCampaign = campaign == null || plan.campaign == campaign
            
            matchesQuery && matchesPrice && matchesCampaign
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updatePriceRange(minPrice: Double, maxPrice: Double) {
        _priceRange.value = minPrice to maxPrice
    }

    fun selectCampaign(campaign: String?) {
        _selectedCampaign.value = campaign
    }

    fun addPlan(plan: HomePlan, imageFile: File) {
        viewModelScope.launch {
            try {
                repository.createPlanWithImage(plan, imageFile)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear el plan: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun updatePlan(plan: HomePlan) {
        viewModelScope.launch {
            try {
                repository.updatePlan(plan)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar el plan: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun deletePlan(plan: HomePlan) {
        viewModelScope.launch {
            try {
                repository.deletePlan(plan)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el plan: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun searchPlans(query: String?, minPrice: Double?, maxPrice: Double?, campaign: String?) {
        viewModelScope.launch {
            try {
                repository.searchPlans(query, minPrice, maxPrice, campaign)
                    .collect { results ->
                        // Los resultados se manejan automáticamente a través del StateFlow plans
                        _errorMessage.value = null
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Error en la búsqueda: ${e.message}"
                e.printStackTrace()
            }
        }
    }
} 