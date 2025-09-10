package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single restaurant.
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
