package com.example.menuapp.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the [RestaurantSelectionScreen].
 *
 * Manages the state for the restaurant ID input field.
 */
class RestaurantSelectionViewModel : ViewModel() {

    // Private mutable state flow to hold the restaurant ID entered by the user.
    private val _restaurantId = MutableStateFlow("")

    /**
     * Publicly exposed, read-only state flow for the restaurant ID.
     * The UI will observe this to react to changes.
     */
    val restaurantId = _restaurantId.asStateFlow()

    /**
     * Called by the UI when the user types in the text field.
     *
     * @param id The new text value from the input field.
     */
    fun onRestaurantIdChange(id: String) {
        // We only want to accept numbers for the ID.
        if (id.all { it.isDigit() }) {
            _restaurantId.value = id
        }
    }
}
