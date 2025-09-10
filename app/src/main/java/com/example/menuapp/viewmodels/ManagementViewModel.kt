package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.Category
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.data.models.Restaurant
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ManagementUiState(
    val restaurant: Restaurant? = null,
    val categories: List<Category> = emptyList(),
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ManagementViewModel(
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagementUiState())
    val uiState = _uiState.asStateFlow()
    private var restaurantId: Long? = null

    init { loadInitialData() }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = repository.getCurrentUser()
            if (user == null) {
                _uiState.update { it.copy(error = "Not authenticated", isLoading = false) }
                return@launch
            }
            val restId = repository.getRestaurantIdForUser(user.id)
            if (restId == null) {
                _uiState.update { it.copy(error = "User not linked to a restaurant", isLoading = false) }
                return@launch
            }
            restaurantId = restId
            refreshData()
        }
    }

    private suspend fun refreshData() {
        restaurantId?.let { id ->
            val restaurant = repository.getRestaurant(id)
            val categories = repository.getCategories(id)
            val menuItems = repository.getMenuItems(id)
            _uiState.update {
                it.copy(restaurant = restaurant, categories = categories, menuItems = menuItems, isLoading = false, error = null)
            }
        }
    }

    fun addCategory(name: String) = viewModelScope.launch {
        restaurantId?.let {
            repository.addCategory(Category(0, name, it))
            refreshData()
        }
    }
    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
        refreshData()
    }
    fun deleteCategory(categoryId: Long) = viewModelScope.launch {
        repository.deleteCategory(categoryId)
        refreshData()
    }

    fun updateMenuItemStock(item: MenuItem, inStock: Boolean) = viewModelScope.launch {
        repository.updateMenuItem(item.copy(inStock = inStock))
        refreshData()
    }

    fun deleteMenuItem(itemId: Long) = viewModelScope.launch {
        repository.deleteMenuItem(itemId)
        refreshData()
    }

    fun addMenuItem(item: MenuItem) = viewModelScope.launch {
        restaurantId?.let {
            repository.addMenuItem(item.copy(restaurantId = it))
            refreshData()
        }
    }

    fun updateMenuItem(item: MenuItem) = viewModelScope.launch {
        repository.updateMenuItem(item)
        refreshData()
    }

    fun saveSettings(name: String, parcelCharge: Double) = viewModelScope.launch {
        restaurantId?.let {
            repository.updateRestaurantDetails(it, name, parcelCharge)
            refreshData()
        }
    }

    fun uploadLogo(bytes: ByteArray, extension: String) = viewModelScope.launch {
        restaurantId?.let {
            println("Simulating logo upload for restaurant $it with $extension file.")
            refreshData()
        }
    }
}
