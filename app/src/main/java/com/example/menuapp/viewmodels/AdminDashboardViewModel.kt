package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val allOrders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class AdminDashboardViewModel(
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = repository.getCurrentUser()
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, error = "Not logged in. Please restart and log in.") }
                return@launch
            }
            val restaurantId = repository.getRestaurantIdForUser(currentUser.id)
            if (restaurantId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not associated with any restaurant.") }
                return@launch
            }
            repository.getOrdersFlow(restaurantId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = "Error listening for orders: ${e.message}") } }
                .collect { orders ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allOrders = orders.sortedByDescending { order -> order.id }
                        )
                    }
                }
        }
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch { repository.updateOrderStatus(orderId, newStatus) }
    }

    fun signOut() {
        viewModelScope.launch { repository.signOut() }
    }
}
