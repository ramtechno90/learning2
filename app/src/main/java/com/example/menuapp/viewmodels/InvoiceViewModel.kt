package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuapp.data.models.CartItem
import com.example.menuapp.data.models.Order
import com.example.menuapp.data.repository.LocalRestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderPlacementState {
    object Idle : OrderPlacementState()
    object Loading : OrderPlacementState()
    data class Success(val order: Order) : OrderPlacementState()
    data class Error(val message: String) : OrderPlacementState()
}

class InvoiceViewModel(
    private val repository: LocalRestaurantRepository = LocalRestaurantRepository()
) : ViewModel() {

    private val _customerName = MutableStateFlow("")
    val customerName = _customerName.asStateFlow()
    private val _orderPlacementState = MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderPlacementState = _orderPlacementState.asStateFlow()

    fun onCustomerNameChange(name: String) { _customerName.value = name }

    fun placeOrder(cartState: CartUiState) {
        viewModelScope.launch {
            _orderPlacementState.value = OrderPlacementState.Loading
            val orderItems = cartState.lineItems.map {
                CartItem(
                    menuItemId = it.menuItem.id,
                    menuItemName = it.menuItem.name,
                    menuItemPrice = it.menuItem.price,
                    dineInQuantity = it.dineInQuantity,
                    takeawayQuantity = it.takeawayQuantity,
                    specialInstructions = it.specialInstructions.takeIf { s -> s.isNotBlank() }
                )
            }
            val orderToPlace = Order(0, _customerName.value, cartState.total, orderItems, "Pending", 0, cartState.restaurant?.id ?: -1)
            if (orderToPlace.restaurantId == -1L) {
                _orderPlacementState.value = OrderPlacementState.Error("Restaurant ID is missing.")
                return@launch
            }
            val result = repository.placeOrder(orderToPlace)
            _orderPlacementState.value = OrderPlacementState.Success(result)
        }
    }

    fun resetOrderState() { _orderPlacementState.value = OrderPlacementState.Idle }
}
