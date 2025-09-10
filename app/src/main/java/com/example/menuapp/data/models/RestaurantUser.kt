package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the link between an admin user and a restaurant from the 'restaurant_users' table.
 *
 * @property userId The unique identifier of the authenticated user (UUID from Supabase Auth).
 * @property restaurantId The ID of the restaurant the user is associated with.
 */
@Serializable
data class RestaurantUser(
    @SerialName("user_id")
    val userId: String,

    @SerialName("restaurant_id")
    val restaurantId: Long
)
