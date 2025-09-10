package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.menuapp.data.repository.LocalRestaurantRepository

@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(
    private val restaurantId: Long,
    private val repository: LocalRestaurantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(restaurantId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
