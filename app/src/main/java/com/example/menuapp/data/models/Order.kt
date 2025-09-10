package com.example.menuapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a customer order.
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
