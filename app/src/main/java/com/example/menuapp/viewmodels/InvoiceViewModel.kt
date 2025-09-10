package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.CartItem
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.repository.LocalRestaurantRepository
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
 * ViewModel for the InvoiceScreen.
 */
class InvoiceViewModel(
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
) : ViewModel() {

    private val _customerName = MutableStateFlow("")
    val customerName = _customerName.asStateFlow()

    private val _orderPlacementState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderPlacementState = _orderPlacementState.asStateFlow()

    fun onCustomerNameChange(name: String) {
        _customerName.value = name
    }

    fun placeOrder(cartState: CartUiState) {
        viewModelScope.launch {
            _orderPlacementState.value = OrderPlacementState.Loading

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

            val orderToPlace = Order(
                id = 0,
                customerName = _customerName.value,
                total = cartState.total,
                items = orderItems,
                status = "Pending",
                dailyOrderNumber = 0,
                restaurantId = cartState.restaurant?.id ?: -1
            )

            if (orderToPlace.restaurantId == -1L) {
                _orderPlacementState.value = OrderPlacementState.Error("Restaurant ID is missing.")
                return@launch
            }

            val result = repository.placeOrder(orderToPlace)
            _orderPlacementState.value = OrderPlacementState.Success(result)
        }
    }

    fun resetOrderState() {
        _orderPlacementState.value = OrderPlacementState.Idle
    }
}
