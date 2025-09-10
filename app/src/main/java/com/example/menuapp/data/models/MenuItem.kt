package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single item on the menu from the 'menu_items' table.
 *
 * @property id The unique identifier for the menu item.
 * @property name The name of the item.
 * @property price The price of the item.
 * @property description A short description of the item.
 * @property category The ID of the category this item belongs to.
 * @property inStock True if the item is currently available, false otherwise.
 * @property takeawayAvailable True if the item can be ordered for takeaway.
 * @property parcelCharge An optional, item-specific charge for takeaway.
 * @property restaurantId The ID of the restaurant this item belongs to.
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
