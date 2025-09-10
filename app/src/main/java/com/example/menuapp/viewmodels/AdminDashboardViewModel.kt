package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Admin Dashboard screen.
 */
data class AdminDashboardUiState(
    val allOrders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel for the [AdminDashboardScreen].
 */
class AdminDashboardViewModel(
    private val repository: RestaurantRepository = RestaurantRepository()
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
                _uiState.update { it.copy(isLoading = false, error = "Not logged in.") }
                return@launch
            }

            val restaurantId = repository.getRestaurantIdForUser(currentUser.id)
            if (restaurantId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not associated with any restaurant.") }
                return@launch
            }

            // Start collecting the real-time flow of orders
            repository.getOrdersFlow(restaurantId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Error listening for orders: ${e.message}") }
                }
                .collect { orders ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allOrders = orders.sortedByDescending { order -> order.id } // Show newest first
                        )
                    }
                }
        }
    }

    /**
     * Updates the status of a given order.
     */
    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            // The real-time flow will automatically emit the updated list,
            // so we don't need to manually refresh the state here.
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}
