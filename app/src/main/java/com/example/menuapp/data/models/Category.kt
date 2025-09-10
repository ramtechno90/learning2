package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a menu category.
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
