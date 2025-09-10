package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RestaurantSelectionViewModel : ViewModel() {
    private val _restaurantId = MutableStateFlow("")
    val restaurantId = _restaurantId.asStateFlow()
    fun onRestaurantIdChange(id: String) {
        if (id.all { it.isDigit() }) _restaurantId.value = id
    }
}
