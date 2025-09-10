package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single item on the menu.
 */
@Serializable
data class MenuItem(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("price")
    val price: Double,

    @SerialName("description")
    val description: String? = null,

    @SerialName("category")
    val category: Long,

    @SerialName("in_stock")
    val inStock: Boolean = true,

    @SerialName("takeaway_available")
    val takeawayAvailable: Boolean = true,

    @SerialName("parcel_charge")
    val parcelCharge: Double? = null,

    @SerialName("restaurant_id")
    val restaurantId: Long
)
