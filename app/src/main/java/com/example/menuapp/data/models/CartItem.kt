package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single item within an order's cart, stored as JSON in the 'orders' table.
 *
 * This is a self-contained summary of the item at the time of purchase.
 *
 * @property menuItemId The ID of the original menu item.
 * @property menuItemName The name of the menu item, stored for easy display.
 * @property menuItemPrice The price of the menu item at the time of the order.
 * @property dineInQuantity The quantity ordered for dine-in.
 * @property takeawayQuantity The quantity ordered for takeaway.
 * @property specialInstructions Any customer-provided special instructions for this item.
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
