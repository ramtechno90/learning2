package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the link between an admin user and a restaurant.
 */
@Serializable
data class RestaurantUser(
    @SerialName("user_id")
    val userId: String,

    @SerialName("restaurant_id")
    val restaurantId: Long
)
