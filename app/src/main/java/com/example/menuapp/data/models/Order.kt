package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a customer order from the 'orders' table.
 *
 * @property id The unique identifier for the order.
 * @property customerName The name of the customer who placed the order.
 * @property total The total cost of the order.
 * @property items A list of [CartItem] objects, stored as a JSONB field in the database.
 * @property status The current status of the order (e.g., "Pending", "Accepted").
 * @property dailyOrderNumber A sequential order number for the day.
 * @property restaurantId The ID of the restaurant this order belongs to.
 */
@Serializable
data class Order(
    @SerialName("id")
    val id: Long,

    @SerialName("customer_name")
    val customerName: String,

    @SerialName("total")
    val total: Double,

    @SerialName("items")
    val items: List<CartItem>,

    @SerialName("status")
    val status: String,

    @SerialName("daily_order_number")
    val dailyOrderNumber: Long,

    @SerialName("restaurant_id")
    val restaurantId: Long
)
