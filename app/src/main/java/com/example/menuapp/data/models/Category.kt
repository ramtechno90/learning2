package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a menu category from the 'categories' table.
 *
 * @property id The unique identifier for the category.
 * @property name The name of the category (e.g., "Appetizers", "Main Course").
 * @property restaurantId The ID of the restaurant this category belongs to.
 */
@Serializable
data class Category(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("restaurant_id")
    val restaurantId: Long
)
