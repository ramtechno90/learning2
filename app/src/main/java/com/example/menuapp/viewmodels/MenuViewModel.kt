package com.example.menuapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.data.models.Restaurant
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MenuUiState(
    val restaurant: Restaurant? = null,
    val categories: List<Category> = emptyList(),
    val menuItems: Map<Long, List<MenuItem>> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class MenuViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
    private val restaurantId: Long = checkNotNull(savedStateHandle["restaurantId"]).toString().toLong()
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMenuData()
    }

    private fun loadMenuData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val restaurantDetails = repository.getRestaurant(restaurantId)
                if (restaurantDetails == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Restaurant not found.")
                    return@launch
                }
                val categories = repository.getCategories(restaurantId)
                val menuItems = repository.getMenuItems(restaurantId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    restaurant = restaurantDetails,
                    categories = categories,
                    menuItems = menuItems.groupBy { it.category },
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load menu: ${e.message}")
            }
        }
    }
}
