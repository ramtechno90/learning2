package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single item within an order's cart.
 */
@Serializable
data class CartItem(
    @SerialName("menu_item_id")
    val menuItemId: Long,

    @SerialName("menu_item_name")
    val menuItemName: String,

    @SerialName("menu_item_price")
    val menuItemPrice: Double,

    @SerialName("dine_in_quantity")
    val dineInQuantity: Int,

    @SerialName("takeaway_quantity")
    val takeawayQuantity: Int,

    @SerialName("special_instructions")
    val specialInstructions: String? = null
)
