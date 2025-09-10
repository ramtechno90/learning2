package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.CartItem
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represents the state of the order placement process.
 */
sealed class OrderPlacementState {
    object Idle : OrderPlacementState()
    object Loading : OrderPlacementState()
    data class Success(val order: Order) : OrderPlacementState()
    data class Error(val message: String) : OrderPlacementState()
}

/**
 * ViewModel for the [InvoiceScreen].
 *
 * Manages the state for the customer's name and handles the final order placement.
 */
class InvoiceViewModel(
    private val repository: RestaurantRepository = RestaurantRepository()
) : ViewModel() {

    private val _customerName = MutableStateFlow("")
    val customerName = _customerName.asStateFlow()

    private val _orderPlacementState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderPlacementState = _orderPlacementState.asStateFlow()

    fun onCustomerNameChange(name: String) {
        _customerName.value = name
    }

    /**
     * Places the final order by sending it to the repository.
     */
    fun placeOrder(cartState: CartUiState) {
        viewModelScope.launch {
            _orderPlacementState.value = OrderPlacementState.Loading

            // 1. Map the ViewModel's CartLineItems to the data model CartItems for the DB
            val orderItems = cartState.lineItems.map { lineItem ->
                CartItem(
                    menuItemId = lineItem.menuItem.id,
                    menuItemName = lineItem.menuItem.name,
                    menuItemPrice = lineItem.menuItem.price,
                    dineInQuantity = lineItem.dineInQuantity,
                    takeawayQuantity = lineItem.takeawayQuantity,
                    specialInstructions = lineItem.specialInstructions.takeIf { it.isNotBlank() }
                )
            }

            // 2. Create the Order object
            // The database will fill in id, status, daily_order_number
            val orderToPlace = Order(
                id = 0, // Will be ignored by DB
                customerName = _customerName.value,
                total = cartState.total,
                items = orderItems,
                status = "Pending", // Default status
                dailyOrderNumber = 0, // Will be set by DB trigger/default
                restaurantId = cartState.restaurant?.id ?: -1
            )

            if (orderToPlace.restaurantId == -1L) {
                _orderPlacementState.value = OrderPlacementState.Error("Restaurant ID is missing.")
                return@launch
            }

            // 3. Call the repository
            val result = repository.placeOrder(orderToPlace)

            // 4. Update the state
            if (result != null) {
                _orderPlacementState.value = OrderPlacementState.Success(result)
            } else {
                _orderPlacementState.value = OrderPlacementState.Error("Failed to place order. Please try again.")
            }
        }
    }

    fun resetOrderState() {
        _orderPlacementState.value = OrderPlacementState.Idle
    }
}
