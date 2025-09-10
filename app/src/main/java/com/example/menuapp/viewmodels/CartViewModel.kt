package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.MenuItem
import com.example.menuapp.data.models.Restaurant
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Represents a single line item in the shopping cart.
 */
data class CartLineItem(
    val menuItem: MenuItem,
    var dineInQuantity: Int = 0,
    var takeawayQuantity: Int = 0,
    var specialInstructions: String = ""
) {
    val totalQuantity: Int
        get() = dineInQuantity + takeawayQuantity
}

/**
 * Represents the overall state of the shopping cart.
 */
data class CartUiState(
    val lineItems: List<CartLineItem> = emptyList(),
    val subtotal: Double = 0.0,
    val parcelCharges: Double = 0.0,
    val total: Double = 0.0,
    val totalItemCount: Int = 0,
    val restaurant: Restaurant? = null
)

/**
 * ViewModel to manage the state of the shopping cart.
 */
class CartViewModel(
    private val restaurantId: Long,
    private val repository: LocalRestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val restaurant = repository.getRestaurant(restaurantId)
            _uiState.update { it.copy(restaurant = restaurant) }
        }
    }

    fun addItem(item: MenuItem) {
        val currentItems = _uiState.value.lineItems.toMutableList()
        val existingItem = currentItems.find { it.menuItem.id == item.id }

        if (existingItem != null) {
            existingItem.dineInQuantity++
        } else {
            currentItems.add(CartLineItem(menuItem = item, dineInQuantity = 1))
        }
        updateCartState(currentItems)
    }

    fun updateDineInQuantity(itemId: Long, quantity: Int) {
        val currentItems = _uiState.value.lineItems.toMutableList()
        val item = currentItems.find { it.menuItem.id == itemId }
        if (item != null && quantity >= 0) {
            item.dineInQuantity = quantity
            updateCartState(currentItems)
        }
    }

    fun updateTakeawayQuantity(itemId: Long, quantity: Int) {
        val currentItems = _uiState.value.lineItems.toMutableList()
        val item = currentItems.find { it.menuItem.id == itemId }
        if (item != null && quantity >= 0) {
            item.takeawayQuantity = quantity
            updateCartState(currentItems)
        }
    }

    fun updateSpecialInstructions(itemId: Long, instructions: String) {
        val currentItems = _uiState.value.lineItems.toMutableList()
        val item = currentItems.find { it.menuItem.id == itemId }
        if (item != null) {
            item.specialInstructions = instructions
            _uiState.update { it.copy(lineItems = item.let{ currentItems }) }
        }
    }

    private fun updateCartState(items: List<CartLineItem>) {
        val finalItems = items.filter { it.totalQuantity > 0 }
        val subtotal = finalItems.sumOf { it.menuItem.price * it.totalQuantity }
        val universalParcelCharge = _uiState.value.restaurant?.universalParcelCharge ?: 0.0

        val parcelCharges = finalItems.sumOf {
            val charge = it.menuItem.parcelCharge ?: universalParcelCharge
            charge * it.takeawayQuantity
        }

        val total = subtotal + parcelCharges
        val totalItemCount = finalItems.sumOf { it.totalQuantity }

        _uiState.update {
            it.copy(
                lineItems = finalItems,
                subtotal = subtotal,
                parcelCharges = parcelCharges,
                total = total,
                totalItemCount = totalItemCount
            )
        }
    }

    fun clearCart() {
        updateCartState(emptyList())
    }
}
