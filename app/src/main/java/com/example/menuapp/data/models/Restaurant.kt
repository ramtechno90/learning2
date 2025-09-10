package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single restaurant from the 'restaurants' table.
 *
 * @property id The unique identifier for the restaurant.
 * @property name The name of the restaurant.
 * @property logoUrl The URL for the restaurant's logo.
 * @property displayPreference A string indicating display preferences (e.g., "grid", "list").
 * @property universalParcelCharge A default parcel charge applied to takeaway items if not specified on the item itself.
 */
@Serializable
data class Restaurant(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("logo_url")
    val logoUrl: String? = null,

    @SerialName("display_preference")
    val displayPreference: String? = null,

    @SerialName("universal_parcel_charge")
    val universalParcelCharge: Double = 0.0
)
