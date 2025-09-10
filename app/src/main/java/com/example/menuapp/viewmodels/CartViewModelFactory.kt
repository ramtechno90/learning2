package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.menuapp.data.repository.RestaurantRepository

/**
 * Factory for creating a [CartViewModel] with a constructor that takes dependencies.
 *
 * This is necessary because ViewModels with constructor arguments cannot be created
 * by the default `viewModel()` delegate without a factory.
 *
 * @param restaurantId The ID of the restaurant for which the cart is being created.
 * @param repository The repository to fetch restaurant data.
 */
@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(
    private val restaurantId: Long,
    private val repository: RestaurantRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(restaurantId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
